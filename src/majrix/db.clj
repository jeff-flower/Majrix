(ns majrix.db)

(defn add-user
  [req-body]
  ; assume req-body is valid for now
  ; generate token
  ; add user to database 
  ; return map with user_id and access token
  ; this function won't be concerned with the proper response formatting
  {:user_id (get req-body "username") :access_token "openSesame123"})
