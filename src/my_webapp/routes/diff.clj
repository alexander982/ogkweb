(ns my-webapp.routes.diff
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes GET]]
            [my-webapp.db.core :as db]
            [my-webapp.layout :as layout]
            [ring.util.http-response :refer [found]]))

(defn diff-page [id1 id2]
  (let [u1 (db/get-unit-by-id {:id id1})
        u2 (db/get-unit-by-id {:id id2})]
    (layout/render "diff/results.html"
                   {:results (db/get-diff-by-ids
                              {:id1 id1 :id2 id2})
                    :unit1 u1
                    :unit2 u2
                    :db-update-date
                    (:v_date (db/get-last-db-update))})))

(defroutes diff-routes
  (GET "/diff/results" [id1 id2] (diff-page id1 id2)))
