(ns my-webapp.routes.plan
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes GET]] 
            [my-webapp.db :as db]
            [my-webapp.layout :as layout])
  (:import [java.util GregorianCalendar]))

(defn plan-index-page
  [year month]
  (layout/render "plan/index.html"
                 {:year year
                  :prev (let [month-1 (dec month)
                              month (if (= 0 month-1) 12 month-1)
                              year (if (= 0 month-1) (dec year) year)]
                          {:year year :month month})
                  :next (let [month+1 (inc month)
                              month (if (= 13 month+1) 1 month+1)
                              year (if (= 13 month+1) (inc year) year)]
                          {:year year :month month})
                  :month month
                  :plans (db/get-plans year
                                       (inc (/ (dec month) 3))
                                       (inc (mod (dec month) 3)))
                  :db-update-date
                  (:v_date (first (db/get-version-date)))}))

(defn current-month-plan [year month]
  (let [year (if (empty? year) 0 (Integer/parseInt year))
        month (if (empty? month) 0 (Integer/parseInt month))]
    (if (some zero? [year month])
      (let [date (GregorianCalendar/getInstance)
            year (.get date GregorianCalendar/YEAR)
            month (inc (.get date GregorianCalendar/MONTH))]
        (plan-index-page year month))
      (plan-index-page year month))))

(defn search-plan []
  (layout/render "plan/search.html" {:years (db/get-plan-years)}))

(defroutes plan-routes
  (GET "/plans" [year  month]
       (current-month-plan year month))
  (GET "/plans/search" []
       (search-plan)))
