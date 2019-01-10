(ns my-webapp.db.core
  (:require
    [conman.core :as conman]
    [mount.core :refer [defstate]]
    [my-webapp.config :refer [env]]))

(defstate ^:dynamic *db*
  :start (conman/connect!
          {:datasource
           (doto (org.h2.jdbcx.JdbcDataSource.)
             (.setURL (env :database-url))
             (.setUser "sa")
             (.setPassword ""))})
  :stop (conman/disconnect! *db*))

(conman/bind-connection *db*
                        "sql/admin.sql"
                        "sql/ather.sql"
                        "sql/ogk.sql")

(defn get-metals [id]
  (update-id-param {:id id})
  (get-metals-by-id))
