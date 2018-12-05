(ns my-webapp.routes.admin
  (:require [my-webapp.db.core :as db]
            [my-webapp.layout :as layout]
            [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes GET POST]]))

(defn users-page
  []
  (layout/render "admin/users.html" {:users (db/get-users {:limit 10
                                                           :offset 0})}))

(defroutes admin-routes
  (GET "/admin/users" []
       (users-page)))
