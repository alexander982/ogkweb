(ns my-webapp.auth.rules
  (:require [buddy.auth :refer [authenticated?]]
            [buddy.auth.accessrules :as acr]))

(defn admin-access
  [{identity :identity}]
  (if (:admin identity)
    (acr/success)
    (acr/error)))

(def rules [{:uri "/admin/*"
             :handler admin-access}
            {:uri "*/edit*"
             :handler authenticated?}])
