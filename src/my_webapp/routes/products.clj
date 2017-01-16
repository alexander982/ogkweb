(ns my-webapp.routes.products
  (:require [clojure.tools.logging :as log]
            [my-webapp.layout :as layout]
            [my-webapp.db :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [found]]))

(defn products-page [{flash :flash}]
  (layout/render "products/index.html" {:error (:error flash)
                                        :message (:message flash)}))

(defn products-results [pref name]
  (if (every? empty? [pref name])
    (do (log/info "GET /products/result has empty params! Redirect to /products")
        (assoc (found (str layout/*app-context* "/products"))
               :flash
               {:error true
                :message "Нужно указать хотябы один параметр поиска!"}))
    (layout/render "products/results.html"
                        {:results (db/get-products pref name)
                         :pref pref
                         :name name
                         :db-update-date (:v_date
                                          (first (db/get-version-date)))})))

(defroutes products-routes
  (GET "/products" req
       (products-page req))
  (GET "/products/results" [pref name]
       (products-results pref name)))
