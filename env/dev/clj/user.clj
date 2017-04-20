(ns user
  (:require [mount.core :as mount] 
            my-webapp.core))

(defn start []
  (mount/start-without #'my-webapp.core/repl-server))

(defn stop []
  (mount/stop-except #'my-webapp.core/repl-server))

(defn restart []
  (stop)
  (start))


