(ns blockchain.components.sample
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]))

(defn test? [service-map]
  (= :test (:env service-map)))

(defrecord SampleComp [service-map service]

  component/Lifecycle

  (stop [this]
    (when (and service (not (test? service-map)))
      (http/stop service))
    (assoc this :service nil))

  (start [this]
    (if service
      this
      (cond-> service-map
              true http/create-server
              (not (test? service-map)) http/start
              true ((partial assoc this :service))))))
