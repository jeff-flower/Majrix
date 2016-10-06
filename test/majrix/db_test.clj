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

(facts "Extracting the neo4j error code"
  (let [parsed-body-with-error {:results [] :errors [{:code "Neo.Some.Error.Code" :message "Some error message"}]}
        parsed-body-with-empty-error {:results [] :errors []}
        parsed-body-with-no-error {:results []}]
    (fact "returns nil if no errors array"
      (db/get-error parsed-body-with-no-error) => nil)
    (fact "returns nil if the errors array is empty"
      (db/get-error parsed-body-with-empty-error) => nil)
    (fact "returns a string if the errors array contains an error map"
      (string? (db/get-error parsed-body-with-error)) => true)))
