(ns majrix.db
  (:require [clj-http.client :as client]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [cheshire.core :as cheshire]))

(def config (-> (io/resource "db.edn")
                slurp
                edn/read-string))

; define a map to hold neo4j endpoints
(def endpoints 
  {:cypher "transaction/commit"})

(defn build-cypher-body
  "Creates a JSON formatted string suitable for Neo4j's HTTP transaction API. As
  stated in Neo4j's [documentation](http://neo4j.com/docs/developer-manual/current/http-api/),
  the transaction body should be formatted as such

    {
      \"statements\": [
        { \"statement\": \"CREATE (u:User {name: 'Alice'})\" }
      ]
    }"
  [statement]
  (cheshire/generate-string {:statements [{:statement statement}]}))

; neo4j errors:
; "Neo.ClientError.Schema.ConstraintValidationFailed" :M_USER_IN_USE

(defn get-error
  "Grabs the first error code from Neo4j's response, if present. Otherwise
  returns nil."
  [{errors :errors}]
  ; 3 possible cases:
  ; 1) error keyword not present in response from neo4j
  ; 2) error keyword present but there is no error (this happens on cypher transaction commit requests
  ; 3) error keyword present and there is an error
  (if (empty? errors)
    nil
    (-> errors
        first
        :code)))

;; api-res-map -> the response object being sent back to the api layer
;; db-res-body -> body of the db response
(defn add-error-response
  "If an error is present in db-res-body, add the error message to api-res-map"
  [api-res-map db-res-body]
  (let [error (get-error (cheshire/parse-string db-res-body true))]
    (if (nil? error)
      ; if no error, return the api-res-map as is
      api-res-map
      ; if error, add error keyword and error message to api-res-map
      ; TODO: convert neo4j error to internal error code (see notes at bottom of code)
      (assoc api-res-map :error error))))

(defn build-api-response
  "Create the response sent back to the API layer"
  [{body :body}]
  (-> {}
      (add-error-response body)))

(defn create-user!
  "Attempts to create a user in the database."
  [user-id home-server]
  (let [database (:database config)
        url (str (:base-url database) (:cypher endpoints))
        username (:username database)
        password (:password database)
        body (format "CREATE (u:User {user_id: '%s', home_server: '%s'})" user-id home-server)]
    (try
      (let [response (client/post url {:basic-auth [username password]
                                       :content-type :json
                                       :body (build-cypher-body body)})]
        (build-api-response response))
      (catch Exception e
        ;; An unsuccessful status code means something went wrong with the
        ;; database connection (system down, unauthorized, etc.). This is
        ;; a nonstandard Matrix error and the API shouldn't respond with
        ;; any information about what went wrong. We will want to log
        ;; what problem occurred.
        {:error :SYSTEM_ERROR}))))

; Slight idea on how we could have contextual mapping of errors going back to
; the API side.
; {:UNIQUE_FAILED "Neo.ClientError.Schema.ConstraintValidationFailed"}
; (build-api-response error {:UNIQUE_FAILED :M_ROOM_IN_USE})
; (build-api-response error {:UNIQUE_FAILED :M_USER_IN_USE})
