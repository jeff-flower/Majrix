(ns majrix.db
  (:require
   [clj-http.client :as client]
   [cheshire.core :as cheshire]))

; this is probably a terrible idea
(def baseUrl "http://hobby-glcdifjnbmnagbkekibbcdnl.dbs.graphenedb.com:24789/db/data/")
(def credentials ["majrix" "6KlWQ4kr7MeUx4LldF1X"])

(def endpoints 
  {:cypher "transaction/commit"})

; required neo4j format: { "statements": { "statement" : ... }}
; (def query "{\"statements\": [ {\"statement\" : \"MATCH (n) RETURN (n)\"}]}")
(def format-start "{\"statements\": [ {\"statement\" : ")
(def format-end "}]}")

; take a user name and build a cypher query to add the user to the db
(defn build-add-user-query
  [user-id]
  (let [qstart "\"CREATE (u:User {:user_id: \""
        qend "\"}) RETURN u.user_id as user_id\""]
    (str format-start qstart user-id qend format-end)))
