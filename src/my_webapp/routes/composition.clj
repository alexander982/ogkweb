(ns my-webapp.routes.composition
  (:require [my-webapp.layout :as layout]
            [my-webapp.db.core :as db]
            [compojure.core :refer [defroutes GET]]))

(defn composition-results [id pref n]
  (let [{:keys [prefix num name]} (if id
                               (db/get-unit-by-id id)
                               {:prefix pref :num n :name nil})]
    (layout/render "composition/results.html"
                   {:results (if id
                               (db/get-composition-by-id id)
                               (db/get-composition prefix num))
                    :pref prefix
                    :num num
                    :name name
                    :db-update-date
                    (:v_date
                     (db/get-last-db-update))})))

(defn occurrence-results [id pref n]
  (let [{:keys [prefix num name]} (if id
                               (db/get-unit-by-id id)
                               {:prefix pref :num n :name nil})]
    (layout/render "occurrence/results.html"
                   {:results (if id
                               (db/get-occurrence-by-id id)
                               (db/get-includes prefix num))
                    :pref prefix
                    :num num
                    :name name
                    :db-update-date
                    (:v_date
                     (db/get-last-db-update))})))

(defroutes composition-routes
  (GET "/composition/results" [id pref num]
       (composition-results id pref num))
  (GET "/occurrence/results" [id pref num]
       (occurrence-results id pref num)))
