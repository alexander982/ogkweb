(ns user
  (:require [mount.core :as mount]
            [conman.core :as conman]
            my-webapp.core))

(defn start []
  (mount/start-without #'my-webapp.core/repl-server))

(defn stop []
  (mount/stop-except #'my-webapp.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn restart-db []
  (mount/stop #'my-webapp.db.core/*db*)
  (mount/start #'my-webapp.db.core/*db*)
  (binding [*ns* 'my-webapp.db.core]
    (conman/bind-connection my-webapp.db.core/*db*
                            "sql/ogk.sql"
                            "sql/ather.sql")))
