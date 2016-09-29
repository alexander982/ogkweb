(ns my-webapp.handler
  (:require [compojure.core :as cc]
            #_[compojure.handler :as handler]
            [compojure.route :as route]
            [my-webapp.db :as db]
            [my-webapp.layout :as layout]
            [my-webapp.views :as views]
            #_[ring.adapter.jetty :as jetty])
  (:gen-class))

(cc/defroutes app-routes
  (cc/GET "/"
          []
          (layout/render "home.html"
                         {:db-update-date (:v_date (db/get-version-date))}))
  (cc/POST "/cont_unit_req"
           {params :params}
           (views/cont-unit-page params))
  (cc/GET "/cont_unit_req"
           [cont-id reqtype]
           (views/cont-unit-page {:cont-id cont-id
                                  :reqtype reqtype}))
  (cc/POST "/search"
           {params :params}
           (views/search-page params))
  (cc/GET "/search"
          []
          (views/search-page {}))
  (cc/POST "/diff"
           {params :params}
           (views/diff-page params))
  (cc/GET "/diff"
          []
          (views/diff-page {}))
  (cc/POST "/metals"
           {params :params}
           (views/metals-page params))
  (cc/GET "/metals"
          []
          (views/metals-page {}))
  (cc/POST "/products"
           {params :params}
           (views/products-page params))
  (cc/GET "/products"
          []
          (views/products-page {}))
  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  app-routes)

#_(defn -main
  [& [port]]
  (let [port (Integer. (or port
                           (System/getenv "PORT")
                           5000))]
    (jetty/run-jetty #'app {:port port
                            :join? false})))
