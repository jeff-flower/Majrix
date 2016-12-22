(ns majrix.handler-test
  (:require [midje.sweet :refer :all] 
            [ring.mock.request :as mock]
            [majrix.handler :refer :all]
            [cheshire.core :as cheshire]))

(facts "compose-response"
  (let [userid "user1"]
    (fact "if no error, response contains no error key"
      (contains? (compose-response userid {}) :error) => false)
    (fact "if error is in db-error-map, response contains error key"
      (contains? (:body (compose-response userid {:error :M_USER_IN_USE})) :error) => true)
    ;; TODO: currently if error not in db-error-map, the response will contain an error with value of nil, we should fix that sometime
    (fact "if error is not in db-error-map, respons contains error key"
      (contains? (:body (compose-response userid {:error :NOT_AN_ERROR})) :error) => true)))

(facts "register route"
  (let [route "/_matrix/client/r0/register"
        username "user1"
        body (cheshire/generate-string {:username username})]
    (facts "successful response"
      (fact "returns status of 200"
        ;;
        (:status (app (-> (mock/request :post route)
                          ;; must set request content type or body content won't be added correctly
                          (mock/content-type "application/json")
                          (mock/body body)))) => 200
        ;; provided is midje mocking function that lays out conditions that must exist for a test to be successful
        ;; note that you must use the full namespace qualified name of var you wish to redefine if the var belongs
        ;; to a namespace different from the namespace you are testing
        (provided (#'majrix.db/create-user! username "majrix") => {}))
      (facts "error response"
        (prerequisite (#'majrix.db/create-user! username "majrix") => {:error :M_USER_IN_USE})
        (fact "returns status of 400"
          (:status (app (-> (mock/request :post route)
                            ;; must set request content type or body content won't be added correctly
                            (mock/content-type "application/json")
                            (mock/body body)))) => 400)
        (fact "returns error message"
          (contains? (app (-> (mock/request :post route)
                              ;; must set request content type or body content won't be added correctly
                              (mock/content-type "application/json")
                              (mock/body body))) :body) => true)))))

