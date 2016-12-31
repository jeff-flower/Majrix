(ns majrix.handler
  "Server definition"
  (:require [cheshire.core :as cheshire]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :as ring-defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :as ring-response :refer [response status]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [majrix.db :as db]))

; load config settings
(def config (-> (io/resource "api.edn")
                slurp
                edn/read-string))

;; A map defining all of the API responses based on error codes returned from
;; the database layer.
(def db-error-map {:M_USER_IN_USE {:status 400
                                   :message "Username in use."}})

(defn encrypt
  "Encrypt the provided string."
  [string]
  string)

(defn decrypt
  "Decrypt the provided string."
  [string]
  string)

(defn generate-access-token
  "Generate an access for token for a given user."
  [username]
  (cheshire/generate-string {:username username}))

(defroutes app-routes
  (context "/_matrix/client/r0" []
           (POST "/register" req (fn [req]
                                   (let [userid (get-in req [:body "username"])]
                                     (-> (db/create-user! userid (:home-server config))
                                         (compose-response {:user_id userid
                                                            :access_token (generate-access-token userid)
                                                            :home_server (:home-server config)})))))
           (POST "/login" req (fn [req] (ring-response/response req)))
           (POST "/createRoom" req (fn [req] (ring-response/response req)))
           (POST "/join/:roomId" [roomId] (str roomId)) 
           (GET "/sync" req (fn [req] req)))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      ring-json/wrap-json-body          ; parse the incoming request to map
      ring-json/wrap-json-response      ; parse the outgoing response to json
      ; wrap-defaults sets up standard ring middleware
      (ring-defaults/wrap-defaults (assoc-in ring-defaults/site-defaults [:security :anti-forgery] false))))
