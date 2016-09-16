(ns majrix.db-test
  (:require [midje.sweet :refer :all] 
            [majrix.db :as db]))

(facts "add-user"
  (let [req {"username" "mike"}]
    (fact "returns a map"
      (instance? clojure.lang.PersistentArrayMap (db/add-user req)) => true)))


