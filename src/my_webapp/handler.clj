(ns my-webapp.handler
  (:require [clojure.tools.logging :as log]
            [compojure.core :as cc]
            [compojure.route :as route]
            [luminus.logger :as logger]
            [mount.core :as mount]
            [my-webapp.config :refer [env]]
            [my-webapp.env :refer [defaults]]
            [my-webapp.layout :as layout]
            [my-webapp.middleware :refer [wrap-base]]
            [my-webapp.routes.auth :refer [auth-routes]]
            [my-webapp.routes.composition :refer [composition-routes]]
            [my-webapp.routes.home :refer [home-routes]]
            [my-webapp.routes.metals :refer [metals-routes]]
            [my-webapp.routes.products :refer [products-routes]]
            [my-webapp.routes.search :refer [search-routes]]
            [my-webapp.routes.plan :refer [plan-routes]]
            )
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

(def app-routes
  (cc/routes
   home-routes
   search-routes
   composition-routes
   metals-routes
   products-routes
   plan-routes
   auth-routes
   (route/not-found
    (:body
     (layout/error-page
      {:status 404
       :title "Страница не найдена!"
       :message "Специально обученные гномы не смогли откопать страницу, которую вы запрашиваете!"})))))

(def app
  (wrap-base #'app-routes))
