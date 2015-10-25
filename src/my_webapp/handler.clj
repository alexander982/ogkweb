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
  (cc/POST "/search_by_name"
           {params :params}
           (views/search-by-name params))
  (cc/GET "/search_by_name"
          []
          (views/search-by-name {}))
  (cc/POST "/search_by_pref_num"
           {params :params}
           (views/search-by-pref-num params))
  (cc/GET "/search_by_pref_num"
          []
          (views/search-by-pref-num {}))
  (cc/POST "/search_by_num"
           {params :params}
           (views/search-by-num params))
  (cc/GET "/search_by_num"
          []
          (views/search-by-num {}))
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
