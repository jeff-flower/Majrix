(ns majrix.db-test
  (:require [midje.sweet :refer :all] 
            [majrix.db :as db]))

(facts "register-user"
  (let [req {"username" "mike"}]
    (fact "returns a map"
      (instance? clojure.lang.PersistentArrayMap (db/register-user req)) => true)))

(facts "generate access token"
  (let [val "jeff"]
    (fact "given a value, returns the same value"
      ;; this is placeholder test for now until we figure out encyrption
      (= val (db/gen-token val)) => true)))

;; NOTE: was going to add test for adding user to db, but that seems like an integration test


