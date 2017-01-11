(ns my-webapp.routes.composition
  (:require [my-webapp.layout :as layout]
            [my-webapp.db :as db]
            [compojure.core :refer [defroutes GET]]))

(defn composition-results [id]
  (let [{:keys [prefix num]} (db/get-unit-by-id id)]
    (layout/render "composition/results.html"
                   {:results (db/get-composition-by-id id)
                    :pref prefix
                    :num num
                    :db-update-date
                    (:v_date
                     (first (db/get-version-date)))})))

(defn occurrence-results [id]
  (let [{:keys [prefix num]} (db/get-unit-by-id id)]
    (layout/render "occurrence/results.html"
                   {:results (db/get-occurrence-by-id id)
                    :pref prefix
                    :num num
                    :db-update-date
                    (:v_date
                     (first (db/get-version-date)))})))

(defroutes composition-routes
  (GET "/composition/results" [id]
       (composition-results id))
  (GET "/occurrence/results" [id]
       (occurrence-results id)))
