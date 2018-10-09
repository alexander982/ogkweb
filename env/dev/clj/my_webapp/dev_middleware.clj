(ns my-webapp.dev-middleware
  (:require [clojure.tools.logging :as log]
            [ring.middleware.reload :refer [wrap-reload]]
            [selmer.middleware :refer [wrap-error-page]]
            [prone.middleware :refer [wrap-exceptions]]))

(defn wrap-log [handler]
  (fn [request]
    (log/debug "request: " request)
    (handler request)))

(defn wrap-dev [handler]
  (-> handler
      wrap-log
      wrap-reload
      wrap-error-page
      wrap-exceptions))
