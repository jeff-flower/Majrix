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

; map a matrix error code returned by the db side to a set of values suitable to return from the api to the user 
(def db-error-map {:M_USER_IN_USE {:status 400
                                   :message "Username in use."}})

(defn generate-access-token
  []
  "x")

; (compose-response user-id db-res-map)
; user-id -> string
; db-res-map -> map returned from call to db.clj
(defn compose-response
  [user-id {error-code :error}]
  (if (nil? error-code)
    ;; no error, send successful response
    ;; ring response function creates map with status of 200, no headers and given body 
    (ring-response/response {:user_id user-id :access_token (generate-access-token) :home_server (:home-server config)})
                                        ; handle error
    (let [error-map (error-code db-error-map)]
      (-> {:error (:message error-map)}
          ring-response/response
          (ring-response/status (:status error-map))))))

(defroutes app-routes
  (POST "/_matrix/client/r0/register" req (fn [req]
                                            (let [userid (get-in req [:body "username"])]
                                              (->>(db/create-user! userid (:home-server config))
                                                  (compose-response userid)))))
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
