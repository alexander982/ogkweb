(ns my-webapp.routes.home
  (:require [my-webapp.layout :as layout]
            [my-webapp.db :as db]
            [compojure.core :refer [defroutes GET]]))

(defn home-page []
  (layout/render
   "home.html" {:db-update-date
                (:v_date
                 (first (db/get-version-date)))}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
