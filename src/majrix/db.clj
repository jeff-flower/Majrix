(ns majrix.db)

(defn register-user
  [req-body]
  ; assume req-body is valid for now
  ; generate token
  ; add user to database 
  ; return map with user_id and access token
  ; this function won't be concerned with the proper response formatting
  (if (add-user req-body) 
    {:user_id (get req-body "username") :access_token (gen-token "OpenSesame123")}
    {:error "add user failed"}))

(defn gen-token
  "Generate an access token"
  [x]
  x)

; return false if addition fails, return some as yet unknown data if successful
(defn add-user
  "Add a user to the database"
  [{username "username" password "password"}]
  (if true "success"))
