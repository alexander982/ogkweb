(ns my-webapp.routes.auth.backend
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
      (if-let [id (:identity (:session request))]
        (do (log/info "there is identity " id)
          id)
        (if-let [token (get-in request
                               [:cookies "remember-token" :value])]
          (do (log/info "there is token" token)
              (:id (first (db/get-user-by-token token))))
          (log/info "nothin here (:"))))
    (-authenticate [_ request data]
      (authfn data))

    proto/IAuthorization
    (-handle-unauthorized [_ request metadata]
      (if unauthorized-handler
        (unauthorized-handler request metadata)
        (if (authenticated? request)
          (http/response "Permision denied" 403)
          (http/response "Unauthorized" 401))))))
