(ns my-webapp.routes.statements
  (:require [my-webapp.layout :as layout]
            [my-webapp.db.core :as db]
            [compojure.core :refer [defroutes GET]]))

(defn statements-result [id firm]
  (let [firm-name (if firm
                    firm
                    "SIEMENS")
        firm-param (str "%" firm-name "%")
        {:keys [prefix num name]} (db/get-unit-by-id {:id id})]
    (db/update-id-param {:id id})
    (layout/render "statement/index.html"
                   {:results (db/get-statement-by-firm {:id id :firm firm-param})
                    :db-update-date (:v_date
                                     (db/get-last-db-update))
                    :pref prefix
                    :num num
                    :name name
                    :firm firm-name})))

(defroutes statements-routes
  (GET "/statements" [id firm]
       (statements-result id firm)))
