(ns my-webapp.routes.composition
  (:require [my-webapp.layout :as layout]
            [my-webapp.db.core :as db]
            [compojure.core :refer [defroutes GET]]))

(defn composition-results [id]
  (let [{:keys [prefix num]} (db/get-unit-by-id {:id id})]
    (layout/render "composition/results.html"
                   {:results (db/get-composition-by-id {:id id})
                    :pref prefix
                    :num num
                    :db-update-date
                    (:v_date
                     (db/get-last-db-update))})))

(defn occurrence-results [id]
  (let [{:keys [prefix num]} (db/get-unit-by-id {:id id})]
    (layout/render "occurrence/results.html"
                   {:results (db/get-occurrence-by-id {:id id})
                    :pref prefix
                    :num num
                    :db-update-date
                    (:v_date
                     (db/get-last-db-update))})))

(defroutes composition-routes
  (GET "/composition/results" [id]
       (composition-results id))
  (GET "/occurrence/results" [id]
       (occurrence-results id)))
