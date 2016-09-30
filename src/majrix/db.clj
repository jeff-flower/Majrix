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
      (client/post url {:basic-auth [username password]
                        :content-type :json
                        :body (build-cypher-json user-id)})
      {:successful? true}
      (catch Exception e
        {:successful? false}))))
