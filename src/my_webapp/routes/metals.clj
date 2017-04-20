(ns my-webapp.routes.metals
  (:require [my-webapp.layout :as layout]
            [my-webapp.db :as db]
            [compojure.core :refer [defroutes GET]]))

(defn metals-page []
  (layout/render "metals/index.html"))

(defn metals-results [id pref num]
  (let [update-date (:v_date
                     (first (db/get-version-date)))]
    (if id
      (let [{:keys [prefix num]} (db/get-unit-by-id id)]
        (layout/render "metals/results.html"
                       {:results (db/get-metals-by-id id)
                        :pref prefix
                        :num num
                        :db-update-date update-date}))
      (layout/render "metals/results.html"
                     {:results (db/get-metals pref num)
                      :pref pref
                      :num num
                      :db-update-date update-date}))))

(defroutes metals-routes
  (GET "/metals" [] (metals-page))
  (GET "/metals/results" [id pref num] (metals-results id pref num)))
