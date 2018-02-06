(ns my-webapp.handler
  (:require [clojure.tools.logging :as log]
            [compojure.core :as cc]
            [compojure.route :as route]
            [luminus.logger :as logger]
            [mount.core :as mount]
            [my-webapp.config :refer [env]]
            [my-webapp.env :refer [defaults]]
            [my-webapp.layout :as layout]
            [my-webapp.middleware :refer [wrap-context wrap-internal-error
                                          wrap-formats]]
            [my-webapp.routes.api :refer [api-routes]]
            [my-webapp.routes.diff :refer [diff-routes]]
            [my-webapp.routes.docs :refer [docs-routes]]
            [my-webapp.routes.composition :refer [composition-routes]]
            [my-webapp.routes.home :refer [home-routes]]
            [my-webapp.routes.metals :refer [metals-routes]]
            [my-webapp.routes.products :refer [products-routes]]
            [my-webapp.routes.search :refer [search-routes]]
            [my-webapp.routes.plan :refer [plan-routes]]
            [ring-ttl-session.core :refer [ttl-memory-store]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]])
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
   (wrap-formats api-routes)
   diff-routes
   home-routes
   search-routes
   composition-routes
   metals-routes
   products-routes
   plan-routes
   docs-routes
   (route/not-found
    (:body
     (layout/error-page
      {:status 404
       :title "Страница не найдена!"
       :message "Специально обученные гномы не смогли откопать страницу, которую вы запрашиваете!"})))))

(def app
  (-> ((:middleware defaults) #'app-routes)
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:session :store] (ttl-memory-store (* 60 30)))))
      wrap-internal-error
      wrap-context))
