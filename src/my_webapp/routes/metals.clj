(ns my-webapp.routes.metals
  (:require [my-webapp.layout :as layout]
            [my-webapp.db.core :as db]
            [compojure.core :refer [defroutes GET]]))

(defn metals-page []
  (layout/render "metals/index.html"))

(defn metals-results [id]
  (let [update-date (:v_date
                     (db/get-last-db-update))
        {:keys [prefix num]} (db/get-unit-by-id {:id id})]
    (layout/render "metals/results.html"
                   {:results (db/get-metals id)
                    :pref prefix
                    :num num
                    :db-update-date update-date})))

(defroutes metals-routes
  (GET "/metals" [] (metals-page))
  (GET "/metals/results" [id] (metals-results id)))
