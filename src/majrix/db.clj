(ns majrix.db
  (:require
   [clj-http.client :as client]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [cheshire.core :as cheshire]))

(def config (edn/read-string (slurp (io/resource "properties.edn"))))

; define a map to hold neo4j endpoints
(def endpoints 
  {:cypher "transaction/commit"})

; return true if the body map contains an empty :errors array
; or if the errors key does not exist in the body
; if i'm reading the neo4j api correctly, cypher transactions will alwyas return a 200 code
; if there are any errors, the erros array will not be empty
; for any other apis, a successful response will return an object with no errors array
; pass the parsed body of a reponse to this function
(defn no-errors?
  [{errors :errors}]
  (if errors 
    ; if errors exists check to see if the array is empty or not
    (empty? errors)
    ; else return true, there was no errors key in the body
    true))

; return true if the response contains no errors
(defn successful-response?
  [res]
  (no-errors? (cheshire/parse-string (:body res) true)))

;;;;; CREATE NEW USER ;;;;;
; neo4j requires cypher query to have the following jsonformat 
; { "statements": [{ "statement" : ... }]}

; take a user name and build a cypher query to add the user to the db
(defn build-add-user-query
  [user-id]
  (format "CREATE (u:User {user_id: \"%s\"}) RETURN u.user_id AS user_id" user-id))

; create the json structure a cypher request needs to have 
(defn build-cypher-json
  [user-id]
  (cheshire/generate-string {:statements [{:statement (build-add-user-query user-id)}]}))

(defn create-user!
  "Attempts to create a user in the database."
  [user-id]
  (let [database (:database config)
        url (str (:base-url database) (:cypher endpoints))
        username (:username database)
        password (:password database)]
    (try
      (let [res (client/post url {:basic-auth [username password]
                                  :content-type :json
                                  :body (build-cypher-json user-id)})]
        {:successful? (successful-response? res)})
      (catch Exception e
        {:successful? false}))))
