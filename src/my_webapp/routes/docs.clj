(ns my-webapp.routes.docs
  (:require [my-webapp.db :as db]
            [my-webapp.layout :refer [error-page]]
            [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes GET]]
            [ring.util.response :refer [response]]
            [ring.util.http-response :refer [content-type ok]]))

(def path-to-docs "//celeron-e3400/arxiv")

(defn get-file-name
  [id]
  (let [doc (first (db/get-doc-by-id id))
        _ (log/debug "found doc: " doc)
        str (str path-to-docs (:fpath doc) (:fname doc))
        _ (log/debug "file name: " str)]
    str))

(defroutes docs-routes
  (GET "/docs" [id]
       (try (-> (clojure.java.io/input-stream (get-file-name id))
                response
                (content-type "image/tiff"))
            (catch java.io.FileNotFoundException e
              (log/warn "File not found " id)
              (error-page
               {:status 404
                :title "Файл не найден!"
                :message "Файл не найден либо архив не доступен"})))))