(ns my-webapp.routes.admin
  (:require [my-webapp.db.core :as db]
            [my-webapp.layout :as layout]
            [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes GET POST]]))

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

(defroutes admin-routes
  (GET "/admin" [] (admin-page))
  (GET "/admin/users" [] (users-page))
  (GET "/admin/updates" [] (updates-page)))
