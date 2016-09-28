(ns majrix.db
  (:require
   [clj-http.client :as client]
   [cheshire.core :as cheshire]
   [majrix.credentials :as creds]))



; define a map to hold neo4j endpoints
(def endpoints 
  {:cypher "transaction/commit"})


;;;;; CREATE NEW USER ;;;;;
; neo4j requires cypher query to have the following jsonformat 
; { "statements": [{ "statement" : ... }]}

; take a user name and build a cypher query to add the user to the db
(defn build-add-user-query
  [user-id]
  (let [qstart "CREATE (u:User {user_id: \""
        qend "\"}) RETURN u.user_id as user_id"]
    (str qstart user-id qend)))

; create the json structure a cypher request needs to have 
(defn build-cypher-json
  [user-id]
  (cheshire/generate-string {:statements [{:statement (build-add-user-query user-id)}]}))

(defn create-user!
  [user-id]
  (client/post 
   (str creds/baseUrl (:cypher endpoints))
   {:basic-auth creds/credentials 
    :content-type :json
    :body (build-cypher-json user-id)}))
