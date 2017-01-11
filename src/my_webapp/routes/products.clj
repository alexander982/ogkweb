(ns my-webapp.routes.products
  (:require [my-webapp.layout :as layout]
            [my-webapp.db :as db]
            [compojure.core :refer [defroutes GET]]))

(defn products-page []
  (layout/render "products/index.html"))

(defn products-results [pref name]
  (layout/render "products/results.html"
                 {:results (db/get-products pref name)
                  :pref pref
                  :name name
                  :db-update-date (:v_date
                                   (first (db/get-version-date)))}))

(defroutes products-routes
  (GET "/products" []
       (products-page))
  (GET "/products/results" [pref name]
       (products-results pref name)))
