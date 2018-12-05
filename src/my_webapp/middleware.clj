(ns my-webapp.middleware
  (:require [clojure.tools.logging :as log]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [my-webapp.env :refer [defaults]]
            [my-webapp.config :refer [env]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [my-webapp.db :as db]
            [my-webapp.layout :refer [*app-context* *identity* error-page]]
            [my-webapp.auth.backend :as b]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring-ttl-session.core :refer [ttl-memory-store]]
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

(defn wrap-formats [handler]
  (let [wrapped (wrap-restful-format
                  handler
                  {:formats [:json :json-kw :transit-json :transit-msgpack]})]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn wrap-internal-error [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable t
        (log/error t)
        (error-page {:status 500
                     :title "Случилось что-то очень плохое!"
                     :message "Отправлена группа специально обученных гномов для устранения проблемы!"})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
   handler
   {:error-response
    (error-page
     {:status 403
      :title "Invalid anti-forgery token"})}))

(defn wrap-identity [handler]
  (fn [request]
    (binding [*identity* (:identity request)]
      (handler request))))

(defn wrap-auth [handler]
  (let [backend (b/backend)]
    (-> handler
        wrap-identity
        (wrap-authentication backend)
        (wrap-authorization backend))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      wrap-auth
      wrap-flash
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:security :anti-forgery] false)
           (assoc-in [:session :store] (ttl-memory-store (* 60 24)))))
      wrap-context
      wrap-internal-error))
