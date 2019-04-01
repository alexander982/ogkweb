(ns my-webapp.routes.params
  (:require [my-webapp.db.core :as db]
            [my-webapp.layout :as layout]
            [clojure.tools.logging :as log]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.coercions :refer [as-int]]
            [ring.util.http-response :refer [found]]))

(defn params-page
  [{flash :flash}]
  (layout/render "params/index.html" {:params (db/get-all-params)
                                      :error (:error flash)
                                      :message (:message flash)}))

(defn create-param!
  [name type]
  ;; todo: validate unique name
  (db/create-param! {:name name :type type})
  (assoc (found (str layout/*app-context* "/params"))
         :flash
         {:message "Параметр успешно создан!"}))

(defn edit-param-page
  [id {flash :flash}]
  (let [{:keys [name type_]} (db/get-param {:id id})]
    (layout/render "params/edit.html" {:id id
                                       :name name
                                       :type type_
                                       :message (:message flash)
                                       :erro (:error flash)})))

(defn edit-param!
  [id name type]
  (db/update-param! {:id id :name name :type type})
  (assoc (found (str layout/*app-context* "/params"))
         :flash {:message "Параметр успешно изменен!"}))

(defn delete-param!
  [id]
  (db/delete-param! {:id id})
  (assoc (found (str layout/*app-context* "/params"))
         :flash {:error "Параметр был удален!"}))

(defroutes params-routes
  (GET "/params" req (params-page req))
  (POST "/params" [name type]
        (create-param! name type))
  (GET "/params/edit" [id :<< as-int  :as req]
       (edit-param-page id req))
  (POST "/params/edit" [id :<< as-int  name type]
        (edit-param! id name type))
  (POST "/params/delete" [id :<< as-int]
        (delete-param! id)))
