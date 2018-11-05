(ns my-webapp.routes.metals
  (:require [my-webapp.layout :as layout]
            [my-webapp.db.core :as db]
            [compojure.core :refer [defroutes GET]]))

(defn metals-page []
  (layout/render "metals/index.html"))

(defn sum-metals [parts]
  (reduce (fn [m part]
            (let [update-fn (fn [old k]
                              (+ old
                                 (* (:qnt part) (k part))))]
              (-> m
                  (update :gold update-fn :gold)
                  (update :silver update-fn :silver)
                  (update :pl update-fn :pl)
                  (update :pal update-fn :pal))))
          {:gold 0.0
           :silver 0.0
           :pl 0.0
           :pal 0.0}
          parts))

(defn metals-results [id]
  (let [update-date (:v_date
                     (db/get-last-db-update))
        {:keys [prefix num]} (db/get-unit-by-id {:id id})
        _ (db/update-id-param {:id  id})
        parts (db/get-parts-with-metals)]
    (layout/render "metals/results.html"
                   {:results {:parts  parts
                              :metals (sum-metals parts)}
                    :pref prefix
                    :num num
                    :db-update-date update-date})))

(defroutes metals-routes
  (GET "/metals" [] (metals-page))
  (GET "/metals/results" [id] (metals-results id)))
