(ns my-webapp.routes.api
  (:require [my-webapp.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [content-type ok]]))

(def archive-path "")

(defn get-docs
  [fname]
  {:archive archive-path
   :docs (db/get-docs-by-fname {:fname (str "%" fname "%")})})

(defn json-content
  [data]
  (content-type
   (ok data)
   "application/json; charset=utf-8"))

(defroutes api-routes
  (GET "/api/docs" [fname]
       (json-content (get-docs fname)))
  (GET "/api/params" [id]
       (json-content (db/get-unit-params {:id id}))))
