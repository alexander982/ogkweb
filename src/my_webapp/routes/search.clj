(ns my-webapp.routes.search
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes GET]]
            [my-webapp.db :as db]
            [my-webapp.layout :as layout]
            [ring.util.http-response :refer [found]]))

(defn search-page [{flash :flash}]
  (layout/render "search.html" {:error (:error flash)
                                :message (:message flash)}))

(defn search-results-page [pref num name {flash :flash}]
  (if (every? empty? [pref num name])
    (do (log/info "GET /search/results has empty params! redirect to /search")
        (assoc (found "/search")
               :flash
               {:error true
                :message "Нужно указать хотябы один параметр поиска!"}))
    (layout/render "search/results.html"
                   {:results (db/get-units pref num name)
                    :pref pref
                    :num num
                    :name name
                    :db-update-date (:v_date
                                     (first (db/get-version-date)))})))

(defroutes search-routes
  (GET "/search" req (search-page req))
  (GET "/search/results" [pref num name :as req]
       (search-results-page pref num name req)))
