(ns majrix.handler
  "Server definition"
  (:require [cheshire.core :as cheshire]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer [response]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [majrix.db :as db]))

; load config settings
(def config (-> (io/resource "api.edn")
                slurp
                edn/read-string))

(defn generate-access-token
  []
  "x")

; (compose-response user-id db-res-map)
; user-id -> string
; db-res-map -> map returned from call to db.clj
(defn compose-response
  [user-id {code :error}]
  (println code)
  (if (nil? code)
    ; no error, send successful response
    (response {:user_id user-id :access_token (generate-access-token) :home_server (:home-server config)})
    ; error
    {:status 400
     :body {:errcode (name code) :error "Desired user ID is already taken."}}))


;; req-body -> map of json request 
(defn register-user
  "Register a user. Currently does not support guest accounts, users must 
  register."
  [req-body]
  ;; - return a 400 if username is invalid, in use, or belongs to the application
  ;;   namespace, or doesn't contain the required properties
  ;; - create schema for the route
  ;; - check error handling at both the api, server, and database levels
  ;; - how to generate access tokens? it correlates to the user, should be unique
  (let [res (db/create-user! (get req-body "username") (:home-server config))]
    (compose-response (get req-body "username") res)))

(defroutes app-routes
  (POST "/_matrix/client/r0/register" req (register-user (:body req)))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      ; wrap-json-body: parse the body of a request with JSON content-type into a map and assign it to the :body key
      wrap-json-body
      ; wrap-json-response: convert a response with a clojure collection as a body into JSON
      wrap-json-response
      ; (wrap-defaults handler site-defaults)
      ; wrap-defaults sets up standard ring middleware
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))
