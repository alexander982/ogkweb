(ns my-webapp.middleware
  (:require [my-webapp.config :refer [env]]
            [my-webapp.layout :refer [*app-context*]]
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
