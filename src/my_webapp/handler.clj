(ns my-webapp.handler
  (:require [compojure.core :as cc]
            [clojure.tools.logging :as log]
            #_[compojure.handler :as handler]
            [compojure.route :as route]
            [luminus.logger :as logger]
            [mount.core :as mount]
            [my-webapp.env :refer [defaults]]
            [my-webapp.config :refer [env]]
            [my-webapp.db :as db]
            [my-webapp.layout :as layout]
            [my-webapp.views :as views]
            #_[ring.adapter.jetty :as jetty])
  (:gen-class))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop ((or (:stop defaults) identity)))

(mount/defstate log
  :start (logger/init (:log-config env)))

(defn init
  "init will be called once when app is deployed as a servlet on an
  app server such as Tomcat put any initialization code here"
  []
  (doseq [component (:started (mount/start))]
    (log/info component "started")))

(defn destroy
  "destroy will be called when your application shuts down, put any
  clean up code here"
  []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents)
  (log/info "my-webapp has shut down!"))

(cc/defroutes app-routes
  (cc/GET "/"
          []
          (layout/render "home.html"
                         {:db-update-date (:v_date
                                           (first (db/get-version-date)))}))
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
