(ns my-webapp.routes.auth
  (:require [my-webapp.db.core :as db]
            [my-webapp.layout :as layout]
            [buddy.hashers :as hashers]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [found]]))

(defn register-form [{flash :flash}]
  (layout/render "auth/register.html" {:errors (:errors flash)
                                       :login (:login flash)
                                       :message (:message flash)}))

(defn register-user [{:keys [login password] :as user}]
  (let [validation
        (b/validate
         user
         :login [v/required
                 [v/min-count 4
                  :message "must be more than 3 chars long"]]
         :password [v/required
                    [v/min-count 6
                     :message "must be more than 6 chars long"]
                    [#(= % (:password1 user))
                     :message "passwords don't match"]]
         :password1 [v/required
                     [v/min-count 6
                      :message "must be more than 6 chars long"]])
        _ (log/info "validation results: " (first validation))
        _ (log/info "user to register: " login)]
    (if (first validation)
      (assoc (found (str layout/*app-context* "/auth/register"))
             :flash
             {:errors (first validation)
              :login login})
      (do (db/create-user! {:login login
                            :pass (hashers/encrypt password)
                            :first-name nil
                            :last-name nil})
          (log/info "user " login " registered")
          (assoc (found (str layout/*app-context* "/auth/login"))
                 :flash
                 {:just-registered? true})))))

(defn login-form [{flash :flash}]
  (let [_ (log/info "flash:" flash)]
    (layout/render "auth/login.html" {:just-registered?
                                      (:just-registered? flash)
                                      :login (:login flash)
                                      :errors (:errors flash)})))

(defn new-token [length]
  (let [r (-> (range 48 58) ;;numeric symbols
              (into (range 65 91))   ;;capital letter
              (into (range 97 122))) ;;smal letter
        b (byte-array length)]
    (doseq [i (range 0 length)]
      (aset-byte b i (rand-nth r)))
    (String. b)))

(defn get-user-token
  [id]
  (let [user (db/get-user {:id id})]
    (if-let [token (:remember_token user)]
      token
      (let [token (new-token 60)]
        (db/update-user-token! {:id id :token token})
        token))))

(defn login-user [login password remember req]
  (let [validation (b/validate {:login login :password password}
                               :login v/required
                               :password v/required)
        _ (log/info "client" (:remote-addr req)
                    "is trying to login with: " login)
        _ (log/debug "validation: " (first validation))
        errors (first validation)]
    (if errors
      (assoc (found (str layout/*app-context* "/auth/login"))
             :flash
             {:login login
              :errors errors})
      (let [user (db/get-user-by-login {:login login})
            wrong-login-password (or (empty? user)
                                     (not (hashers/check password
                                                         (:pass user))))
            _ (log/debug "user: " user)
            _ (log/debug "password OK? " (hashers/check password
                                                        (:pass user)))]
        (if wrong-login-password
          (assoc (found (str layout/*app-context* "/auth/login"))
                 :flash
                 {:login login
                  :errors {:login (list "wrong login or password")
                           :password (list "wrong login or password")}})
          (let [_ (log/info "user: " login " successfully login")
                resp (assoc-in (found (str layout/*app-context* "/"))
                               [:session :identity]
                               user)]
            (if (= remember "true")
              (assoc-in resp [:cookies "remember-token"]
                        {:value (get-user-token (:id user))
                         :path "/"
                         :http-only true
                         :max-age (* 30 24 60 60)})
              resp)))))))

(defn logout-user [req]
  (log/info "cookie:" (:cookies req))
  (log/info "user" layout/*identity* "logged out")
  (let [resp (found (str layout/*app-context* "/"))] 
    (-> resp
        (assoc-in [:session :identity] nil)
        (assoc-in [:cookies "remember-token"] {:max-age 1
                                               :value ""
                                               :path "/"}))))

(defroutes auth-routes
  (GET "/auth/register" req (register-form req))
  (POST "/auth/register" [login password password1]
        (log/info "register form posted")
        (register-user {:login login
                        :password password
                        :password1 password1}))
  (GET "/auth/login" req (login-form req))
  (POST "/auth/login" [login password remember :as req]
        (login-user login password remember req))
  (POST "/auth/logout" req (logout-user req)))