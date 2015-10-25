(ns my-webapp.handler
  (:require [my-webapp.views :as views]
            [compojure.core :as cc]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.adapter.jetty :as jetty]))

(cc/defroutes app-routes
  (cc/GET "/"
          []
          (views/home-page))
  (cc/POST "/cont_unit_req"
           {params :params}
           (views/cont-unit-page params))
  (cc/GET "/cont_unit_req"
           []
           (views/cont-unit-page {}))
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
  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  (handler/site app-routes))

(defn -main
  [& [port]]
  (let [port (Integer. (or port
                           (System/getenv "PORT")
                           5000))]
    (jetty/run-jetty #'app {:port port
                            :join? false})))
