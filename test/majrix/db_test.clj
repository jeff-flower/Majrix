(ns majrix.db-test
  (:require [cheshire.core :as cheshire]
            [clj-http.fake :as cljfake]
            [midje.sweet :refer :all]
            [majrix.db :as db]))

(facts "Buliding the cypher request"
  (let [statement "CREATE (u:User {name: 'Alice'})"
        output (db/build-cypher-body statement)]
    (fact "Has the statement in statements[0].statement."
      (->
        (cheshire/parse-string output true)
        :statements
        first
        :statement) => statement)))

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

(facts "Adding the error-response"
  (let [string-response-with-error (cheshire/generate-string {:results [] :errors [{:code "Neo.ClientError.Schema.ConstraintValidationFailed" :message "Some error message"}]})
        string-response-unknown-error (cheshire/generate-string {:results [] :errors [{:code "Neo.Some.Error.Code" :message "Some error message"}]})
        string-response-empty-error (cheshire/generate-string {:results [] :errors []})
        string-response-no-error (cheshire/generate-string {:results []})]
    (fact "With an error"
      (contains? (db/add-error-response {} string-response-with-error) :error) => true)
    (fact "With an error that isn't accounted for"
      (contains? (db/add-error-response {} string-response-unknown-error) :error) => true)
    (fact "Without an error"
      (contains? (db/add-error-response {} string-response-no-error) :error) => false)
    (fact "With an empty error"
      (contains? (db/add-error-response {} string-response-empty-error) :error) => false)))

(facts "Creating a user"
  (fact "Valid use case"
    (cljfake/with-fake-routes
      {#"^http://.*/transaction/commit$" {:post (fn [req] {:status 200 :body "{\"statements\": [{\"statement\": \"CREATE (u:User {user_id: 'gonads', home_server: 'and strife'})\",\"parameters\": null,\"resultDataContents\": [\"row\",\"graph\"],\"includeStats\": true}]}"})}}
      (db/create-user! "foobar" "homeserver") => {}))
  (fact "Invalid use case"
    (cljfake/with-fake-routes
      {#"^http://.*/transaction/commit$" {:post (fn [req] {:status 200 :body "{\"results\":[], \"errors\":[{\"code\":\"Neo.Some.Error.Code\",\"message\":\"Some error message\"}]}"})}}
      (contains? (db/create-user! "foobar" "homeserver") :error) => true)))
