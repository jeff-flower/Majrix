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

(facts "check the response body for errors"
  (let [no-errors {}
        empty-errors {:errors []}
        yes-errors {:errors [{:error "some error"}]}]
    (fact "return true if no errors keyword"
      (db/no-errors? no-errors) => true)
    (fact "return true if the errors array is empty"
      (db/no-errors? empty-errors) => true)
    (fact "return false if the errors array is not empty"
      (db/no-errors? yes-errors) => false)))
