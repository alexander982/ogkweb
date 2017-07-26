(ns my-webapp.middleware
  (:require [clojure.tools.logging :as log]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [my-webapp.config :refer [env]]
            [my-webapp.db :as db]
            [my-webapp.layout :refer [*app-context* *identity* error-page]]
            [my-webapp.routes.auth.backend :as b]
            )
  (:import [javax.servlet ServletContext]))

(defn wrap-context [handler]
  (fn [request]
    (binding [*app-context*
              (if-let [context (:servlet-context request)]
                (try (.getContextPath ^ServletContext context)
                     (catch IllegalArgumentException _ context))
                (:app-context env))]
      (handler request))))

(defn wrap-internal-error [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable t
        (log/error t)
        (error-page {:status 500
                     :title "Случилось что-то очень плохое!"
                     :message "Отправлена группа специально обученных гномов для устранения проблемы!"})))))

(defn wrap-identity [handler]
  (fn [request]
    (let [id (if-let [identity (get-in request [:session :identity])]
               identity
               (if-let [token (get-in request
                                      [:cookies "remember-token" :value])]
                 (:id (db/get-user-by-token {:token token}))))]
      (binding [*identity* id]
               (handler request)))))

(defn wrap-auth [handler]
  (-> handler
      wrap-identity
      (wrap-authentication b/backend)
      (wrap-authorization b/backend)))
