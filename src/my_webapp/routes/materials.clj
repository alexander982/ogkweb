(ns my-webapp.routes.materials
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes GET]]
            [my-webapp.db.core :as db]
            [my-webapp.layout :as layout]
            [ring.util.http-response :refer [found]]))


(defn materials-page
  [id]
  (let [{:keys [prefix num name]} (db/get-unit-by-id {:id  id})]
    (layout/render "materials/results.html"
                   {:results (db/get-materials {:id id})
                    :pref prefix
                    :num num
                    :name name
                    :db-update-date
                    (:v_date
                     (db/get-last-db-update))})))

(defroutes materials-routes
  (GET "/materials" [id]
       (materials-page id)))
