(ns blockchain.system
  (:require [com.stuartsierra.component :as component]
            [com.stuartsierra.component.repl
             :refer [reset set-init start stop system]]
            [blockchain.http.routes :refer [routes]]
            [io.pedestal.http :as http]
            [blockchain.components.pedestal :as ss]))

(defn new-system
  [env]
  (component/system-map
    :service-map
    {
     :env          env
     ::http/routes routes
     ::http/type   :jetty
     ::http/port   8082
     ::http/join?  false
     }

    :pedestal
    (component/using
      (ss/new-pedestal)
      [:service-map])
    ))

(set-init (constantly (new-system :prod)))

(defn main []
  (component/start (new-system :prod)))

(main)
