(ns my-webapp.env
  (:require [clojure.tools.logging :as log]
            [my-webapp.config :refer [env]]
            [luminus-migrations.core :as migrations]))

(def defaults
  {:init
   (fn []
     (let [db-url (select-keys env [:database-url])]
       (when (> (count (migrations/migrate ["pending"]
                                         db-url))
              0)
         (migrations/migrate ["migrate"] db-url)))
     (log/info "\n-=[ogk-web started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[ogk-web has shut down successfully]=-"))
   :middleware identity})
