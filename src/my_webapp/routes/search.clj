(ns my-webapp.routes.search
  (:require [my-webapp.layout :as layout]
            [my-webapp.db :as db]
            [compojure.core :refer [defroutes GET]]))

(defn search-page []
  (layout/render "search.html"))

(defn search-results-page [pref num name]
  (layout/render "search/results.html"
                 {:results (db/get-units pref num name)
                  :pref pref
                  :num num
                  :name name
                  :db-update-date (:v_date
                                   (first (db/get-version-date)))}))

(defroutes search-routes
  (GET "/search" [] (search-page))
  (GET "/search/results" [pref num name]
       (search-results-page pref num name)))
