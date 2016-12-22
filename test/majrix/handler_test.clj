(ns majrix.handler-test
  (:require [midje.sweet :refer :all] 
            [ring.mock.request :as mock]
            [majrix.handler :refer :all]))

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
  (facts "successful response"
    (fact "returns status of 200"
      ;; TODO: add correct parameters to mock request and handle appropriately in prerequisite
      (prerequisite (#'majrix.db/create-user! ..userid.. ..homeserver..) => {})
      (:status (app (mock/request :post "/_matrix/client/r0/register"))) => 200)
    (facts "error response"
      (fact "returns status of 400")
      (fact "returns error message"))))
