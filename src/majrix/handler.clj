(ns majrix.handler
  "Server definition"
  (:require [cheshire.core :as cheshire]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [majrix.db :as db]))

(def config (-> (io/resource "api.edn")
                slurp
                edn/read-string))

(defn generate-access-token
  []
  "x")

(defn compose-response
  [user-id code]
  (print code)
  (if (nil? code)
    (cheshire/generate-string {:user_id user-id :access_token (generate-access-token) :home_server (:home-server config)})
    {:status 400
     :body (cheshire/generate-string {:errcode (name code) :error "Desired user ID is already taken."})}))

(defn register-user
  "Register a user. Currently does not support guest accounts, users must 
  register."
  [req-body]
  ;; - return a 400 if username is invalid, in use, or belongs to the application
  ;;   namespace, or doesn't contain the required properties
  ;; - create a protocol that defines interactions with a database
  ;; - create a class that implements these protocols
  ;; - create schema for the route
  ;; - check error handling at both the api, server, and database levels
  ;; - how to generate access tokens? it correlates to the user, should be unique
  ;; - write simple solutions, make notes about fixmes
  (let [res (db/create-user! (get req-body "username") (:home-server config))]
    (compose-response (get req-body "username") res)))

(defroutes app-routes
  (POST "/_matrix/client/r0/register" req (register-user (:body req)))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults (wrap-json-body app-routes) (assoc-in site-defaults [:security :anti-forgery] false)))
