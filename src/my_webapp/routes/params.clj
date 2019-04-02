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

(defn unit-params-page
  [id flash]
  (layout/render "params/values/index.html" {:unit
                                             (db/get-unit-by-id {:id id})
                                             :params
                                             (db/get-all-params)
                                             :unit-params
                                             (db/get-unit-params {:id id})
                                             :message (:message flash)}))

(defn add-unit-params!
  [unit-id param-id user-id referer]
  (db/add-unit-params! {:unit-id unit-id
                       :param-id param-id
                       :updated-by user-id
                       :value nil})
  (assoc (found referer)
         :flash {:message "Параметр добавлен!"}))

(defn edit-unit-params-page
  [id flash]
  (layout/render "params/values/edit.html" {:unit
                                            (db/get-unit-by-id {:id id})
                                            :params
                                            (db/get-all-params)
                                            :unit-params
                                            (db/get-unit-params {:id id})
                                            :message (:message flash)}))
(defn update-unit-param!
  [unit-id param-id value user-id]
  (db/update-unit-param! {:unit-id unit-id
                          :param-id param-id
                          :value value
                          :updated-by user-id})
  (assoc (found (str layout/*app-context* "/unit/params/edit?id=" unit-id))
         :flash {:message "Значение сохранено"}))

(defn delete-unit-param!
  [unit-id param-id]
  (db/delete-unit-param! {:unit-id unit-id
                          :param-id param-id})
  (assoc (found (str layout/*app-context* "/unit/params/edit?id=" unit-id))
         :flash {:error "Значение удалено"}))

(defroutes params-routes
  (GET "/params" req (params-page req))
  (POST "/params" [name type]
        (create-param! name type))
  (GET "/params/edit" [id :<< as-int  :as req]
       (edit-param-page id req))
  (POST "/params/edit" [id :<< as-int  name type]
        (edit-param! id name type))
  (POST "/params/delete" [id :<< as-int]
        (delete-param! id))
  (GET "/unit/params" [id :<< as-int :as {flash :flash}]
       (unit-params-page id flash))
  (POST "/unit/params" [id :<< as-int
                        param :<< as-int
                        :as {{user-id :id} :identity {referer "referer"} :headers}]
        (add-unit-params! id param user-id referer))
  (GET "/unit/params/edit" [id :<< as-int :as {flash :flash}]
       (edit-unit-params-page id flash))
  (POST "/unit/params/edit" [unit-id
                             param-id
                             value
                             :as {{user-id :id} :identity}]
        (update-unit-param! unit-id param-id value user-id))
  (POST "/unit/params/delete" [unit-id :<< as-int
                               param-id :<< as-int]
        (delete-unit-param! unit-id param-id)))
