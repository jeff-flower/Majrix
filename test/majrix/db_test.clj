(ns majrix.db-test
  (:require [cheshire.core :as cheshire]
            [midje.sweet :refer :all] 
            [majrix.db :as db]))

(facts "build-http-body"
  (let [single-statment (db/build-http-body "CREATE (u:User {name: 'Alice'})")]
    (fact "Creates a body with a single statement"
      (-> (cheshire/parse-string single-statment true)
          :statements
          count) => 1)))

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
