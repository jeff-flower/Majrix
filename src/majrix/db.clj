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

; TODO: 
; 1) create a map between neo4j errors and majrix error codes
; 2) create a function that passes the response body to get-error,
; takes the result and returns a map with keyword :error_code and value
; of the result from get-error
; 3) integrate the new function with create-user! 

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

(defn get-error
  "Grabs the first error code from Neo4j's response, if present. Otherwise
  returns nil."
  [{body :body}]
  (let [error (cheshire/parse-string body true)]
    (if (empty? error)
      nil
      (-> error
          first
          :code))))

(defn build-api-response
  "Create the response for going back to the API layer. Will return an empty map
  if no errors are found, otherwise it ."
  [error]
  (if (nil? error)
    {}
    {:error (condp = (get-error response)
              "Neo.ClientError.Schema.ConstraintValidationFailed" :M_USER_IN_USE
              :SYSTEM_ERROR)}))

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
                                       :body (build-cypher-body body)})
            error (get-error response)]
        (build-api-response error))
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
