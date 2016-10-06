(ns majrix.db
  (:require [clj-http.client :as client]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [cheshire.core :as cheshire]))

(def config (-> (io/resource "properties.edn")
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
  [{error :errors}]
  (if (empty? error)
    nil
    (-> error
        first
        :code)))

(defn create-user!
  "Attempts to create a user in the database."
  [user-id]
  (let [database (:database config)
        url (str (:base-url database) (:cypher endpoints))
        username (:username database)
        password (:password database)
        body (format "CREATE (u:User {user_id: '%s'})" user-id)]
    (try
      (let [response (client/post url {:basic-auth [username password]
                                       :content-type :json
                                       :body (build-cypher-body body)})]
        {:error (condp = (get-error response)
                  "Neo.ClientError.Schema.ConstraintValidationFailed" :M_USER_IN_USE)})
      (catch Exception e
        ;; An unsuccessful status code means something went wrong with the
        ;; database connection (system down, unauthorized, etc.). This is
        ;; a nonstandard Matrix error and the API shouldn't respond with
        ;; any information about what went wrong. We will want to log
        ;; what problem occurred.
        {:error :SYSTEM_ERROR}))))
