(defproject majrix "0.1.0-SNAPSHOT"
  :description "A Matrix chat client written in Clojure"
  :url "https://www.github.com/jeff-flower/Majrix"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [midje "1.8.3"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-marginalia "0.8.0"]]
  :ring {:handler majrix.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
