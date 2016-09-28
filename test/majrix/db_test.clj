(ns majrix.db-test
  (:require [midje.sweet :refer :all] 
            [majrix.db :as db]))

(facts "build-add-user-query"
  (let [query (db/build-add-user-query "testyMcTestFace")]
    (fact "returns a string"
      (string? query) => true)))

(facts "build-cypher-json"
  (let [json-str (db/build-cypher-json "bimbleTime")]
    (string? json-str) => true))
