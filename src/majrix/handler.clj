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

; map a matrix error code returned by the db side to a set of values suitable to
; return from the api to the user
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

(defn compose-response
  [{error-code :error} api-res]
  (if (nil? error-code)
    (ring-response/response api-res)
    (let [error-map (error-code db-error-map)]
      (-> {:error (:message error-map)}
          (ring-response/response)
          (ring-response/status (:status error-map))))))

(defroutes app-routes
  ;; TODO create a route to login, create a room, join room, get
  ;; message, and send message. Recommended to work on the handle
  ;; portion first before moving onto the database side. Checkout the
  ;; Matrix spec and Client-Server guide for more information/help.
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
      ; wrap-json-body: parse the body of a request with JSON content-type into a map and assign it to the :body key
      ring-json/wrap-json-body
      ; wrap-json-response: convert a response with a clojure collection as a body into JSON
      ring-json/wrap-json-response
      ; (wrap-defaults handler site-defaults)
      ; wrap-defaults sets up standard ring middleware
      (ring-defaults/wrap-defaults (assoc-in ring-defaults/site-defaults [:security :anti-forgery] false))))
