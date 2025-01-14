(ns my-webapp.routes.admin
  (:require [my-webapp.db.core :as db]
            [my-webapp.layout :as layout]
            [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [found]]))

(defn admin-page
  []
  (layout/render "admin/admin.html"))

(defn users-page
  []
  (layout/render "admin/users.html" {:users (db/get-users {:limit 10
                                                           :offset 0})}))

(defn updates-page
  []
  (layout/render "admin/updates.html" {:updates (db/get-updates)}))

(defn database-page
  [{flash :flash}]
  (layout/render "admin/dbase.html" {:message (:message flash)}))

(defn backup-database
  []
  (db/backup-database)
  (assoc (found (str layout/*app-context* "/admin/base"))
             :flash
             {:message {:backup ["Резервная копия создана."]}}))

(defn update-version-date
  [id date]
  (db/update-version-date {:id id :date date})
  (assoc (found (str layout/*app-context* "/admin/updates"))
         :flash
         {:message {:update ["Дата изменена."]}}))

(defn params-page
  []
  (layout/render "admin/params.html" {:results (db/get-all-params)}))

(defn values-page
  []
  (layout/render "admin/param-values.html" {:results (db/get-values)}))

(defroutes admin-routes
  (GET "/admin" [] (admin-page))
  (GET "/admin/base" req (database-page req))
  (POST "/admin/backup" [] (backup-database))
  (GET "/admin/users" [] (users-page))
  (GET "/admin/updates" [] (updates-page))
  (POST "/admin/updates" [id date] (update-version-date id date))
  (GET "/admin/params" [] (params-page))
  (GET "/admin/values" [] (values-page)))
