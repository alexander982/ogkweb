(ns my-webapp.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[ogk-web started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[ogk-web has shut down successfully]=-"))
   :middleware identity})
