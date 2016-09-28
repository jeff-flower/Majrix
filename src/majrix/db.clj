(ns majrix.db
  (:require
   [clj-http.client :as client]
   [cheshire.core :as cheshire]))

; this is probably a terrible idea
(def baseUrl "http://hobby-glcdifjnbmnagbkekibbcdnl.dbs.graphenedb.com:24789/db/data/")
(def credentials ["majrix" "6KlWQ4kr7MeUx4LldF1X"])

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
   (str baseUrl (:cypher endpoints))
   {:basic-auth credentials
    :content-type :json
    :body (build-cypher-json user-id)}))
