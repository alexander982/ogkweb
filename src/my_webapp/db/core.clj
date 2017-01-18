(ns mpm-web.db.core
  (:require
    [conman.core :as conman]
    [mount.core :refer [defstate]]
    [mpm-web.config :refer [env]]))

(defstate ^:dynamic *db*
          :start (conman/connect!
                   {:datasource
                    (doto (org.h2.jdbcx.JdbcDataSource.)
                          (.setURL (env :database-url))
                          (.setUser "sa")
                          (.setPassword ""))})
  :stop (conman/disconnect! *db*))

(conman/bind-connection *db*
                        "sql/ather.sql"
                        "sql/ogk.sql")
