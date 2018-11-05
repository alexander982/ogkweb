(ns my-webapp.routes.api
  (:require [my-webapp.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [content-type ok]]))

(def archive-path "")

(defn get-docs
  [fname]
  {:archive archive-path
   :docs (db/get-docs-by-fname {:fname (str "%" fname "%")})})

(defroutes api-routes
  (GET "/api/docs" [fname]
       (content-type
        (ok
         (get-docs fname)) "application/json; charset=utf-8")))
