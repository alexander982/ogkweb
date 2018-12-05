(ns my-webapp.auth.backend
  (:require [my-webapp.db :as db]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.http :as http]
            [buddy.auth.protocols :as proto]
            [clojure.tools.logging :as log]))

(defn backend
  [& {:keys [unauthorized-handler authfn] :or {authfn identity}}]
  (reify
    proto/IAuthentication
    (-parse [_ request]
      (let [_ (log/debug "session: " (:session request))]
        (if-let [id (:identity (:session request))]
          (do (log/debug "there is identity " id)
              id)
          (if-let [token (get-in request
                                 [:cookies "remember-token" :value])]
            (let [_ (log/debug "there is token" token)
                  id (:id (first (db/get-user-by-token token)))
                  _ (log/debug "user id: " id)]
              id)
            (log/debug "not authenticated")))))
    (-authenticate [_ request data]
      (authfn data))

    proto/IAuthorization
    (-handle-unauthorized [_ request metadata]
      (if unauthorized-handler
        (unauthorized-handler request metadata)
        (if (authenticated? request)
          (http/response "Permision denied" 403)
          (http/response "Unauthorized" 401))))))
