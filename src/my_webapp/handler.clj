(ns my-webapp.handler
  (:require [compojure.core :as cc]
            [clojure.tools.logging :as log] 
            [compojure.route :as route]
            [luminus.logger :as logger]
            [mount.core :as mount]
            [my-webapp.env :refer [defaults]]
            [my-webapp.config :refer [env]]
            [my-webapp.db :as db]
            [my-webapp.layout :as layout]
            [my-webapp.middleware :refer [wrap-context wrap-internal-error]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring-ttl-session.core :refer [ttl-memory-store]])
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

  (cc/GET "/composition/results"
          [id]
          (log/info "GET composition/results?id=" id)
          (let [{:keys [prefix num]} (db/get-unit-by-id id)]
            (layout/render "composition/results.html"
                           {:results (db/get-composition-by-id id)
                            :pref prefix
                            :num num
                            :db-update-date
                            (:v_date
                             (first (db/get-version-date)))})))
  
  (cc/GET "/occurrence/results"
          [id]
          (log/info "GET occurrence/results?id=" id)
          (let [{:keys [prefix num]} (db/get-unit-by-id id)]
            (layout/render "occurrence/results.html"
                           {:results (db/get-occurrence-by-id id)
                            :pref prefix
                            :num num
                            :db-update-date
                            (:v_date
                             (first (db/get-version-date)))})))
  (cc/GET "/search"
          []
          (layout/render "search.html"))
  (cc/GET "/search/results"
          [pref num name]
          (log/info "search/results?pref=" pref "&num=" num "&name=" name)
          (layout/render "search/results.html"
                         {:results (db/get-units pref num name)
                          :pref pref
                          :num num
                          :name name
                          :db-update-date (:v_date
                                           (first (db/get-version-date)))}))

  (cc/GET "/metals"
          []
          (layout/render "metals/index.html"))
  (cc/GET "/metals/results"
          [id pref num]
          (log/info "GET metals/results?id=" id "&pref=" pref "&num" num)
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

  (cc/GET "/products"
          []
          (layout/render "products/index.html"))
  (cc/GET "/products/results"
          [pref name]
          (log/info "products/results?pref=" pref "&name=" name)
          (layout/render "products/results.html"
                         {:results (db/get-products pref name)
                          :pref pref
                          :name name
                          :db-update-date (:v_date
                                           (first (db/get-version-date)))}))
  (cc/GET "/about"
          []
          (layout/render "about.html"))
  #_(route/resources "/")
  (route/not-found (:body
                    (layout/error-page
                     {:status 404
                      :title "Страница не найдена!"
                      :message "Специально обученные гномы не смогли откопать страницу, которую вы запрашиваете!"}))))


(def app
  (-> ((:middleware defaults) #'app-routes)
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:session :store] (ttl-memory-store (* 60 30)))))
      wrap-context
      wrap-internal-error))

