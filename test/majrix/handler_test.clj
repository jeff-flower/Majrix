(ns majrix.handler-test
  (:require [midje.sweet :refer :all] 
            [ring.mock.request :as mock]
            [majrix.handler :refer :all]))

(facts "Some example tests"
  (fact "this is true"
    (= 1 1) => true)
  (fact "this is false"
    (= 1 0) => false)
  (fact "fix me, i fail"
    (= 1 1) => false))

(facts "Main route" 
  (fact "returns 200 and 'hello world' for register route"
    (let [response (app (mock/request :post "/_matrix/client/r0/register"))]
      (= (:status response) 200) => true)
  (fact "returns 200 and 'hello world' for valid route request"
    (let [response (app (mock/request :get "/"))]
      (= (:status response) 200) => true 
      (= (:body response) "Hello World") => true))
   (fact "returns 404 for invalid route request"
    (let [response (app (mock/request :get "/invalid"))]
      (= (:status response) 404) => true)))
