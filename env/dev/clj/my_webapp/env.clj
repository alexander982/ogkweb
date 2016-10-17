(ns my-webapp.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [my-webapp.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[mpm-web started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[mpm-web has shut down successfully]=-"))
   :middleware wrap-dev})
