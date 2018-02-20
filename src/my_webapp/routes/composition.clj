(ns my-webapp.routes.composition
  (:require [my-webapp.layout :as layout]
            [my-webapp.db :as db]
            [compojure.core :refer [defroutes GET]]))

(defn composition-results [id pref n]
  (let [{:keys [prefix num]} (if id
                               (db/get-unit-by-id id)
                               {:prefix pref :num n})]
    (layout/render "composition/results.html"
                   {:results (if id
                               (db/get-composition-by-id id)
                               (db/get-composition prefix num))
                    :pref prefix
                    :num num
                    :db-update-date
                    (:v_date
                     (first (db/get-version-date)))})))

(defn occurrence-results [id pref n]
  (let [{:keys [prefix num]} (if id
                               (db/get-unit-by-id id)
                               {:prefix pref :num n})]
    (layout/render "occurrence/results.html"
                   {:results (if id
                               (db/get-occurrence-by-id id)
                               (db/get-includes prefix num))
                    :pref prefix
                    :num num
                    :db-update-date
                    (:v_date
                     (first (db/get-version-date)))})))

(defroutes composition-routes
  (GET "/composition/results" [id pref num]
       (composition-results id pref num))
  (GET "/occurrence/results" [id pref num]
       (occurrence-results id pref num)))
