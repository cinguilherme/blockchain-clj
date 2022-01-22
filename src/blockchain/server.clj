(ns blockchain.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.content-negotiation :as negotiation]
            [io.pedestal.http.route :as route]
            [blockchain.http.handlers :refer :all]
            [blockchain.http.interceptors :as bb.interceptors]
            [io.pedestal.http.body-params :as body-params]))

(def supported-types ["text/html" "application/edn" "text/plain" "application/json"])
(def content-neg (negotiation/negotiate-content supported-types))

(def routes
  (route/expand-routes
    #{
      ["/hello" :get [content-neg bb.interceptors/coerce-body hello-handler] :route-name :hello]
      ["/hellop" :post [(body-params/body-params) hello-handler] :route-name :hellop]
      ["/echo" :get [bb.interceptors/echo] :route-name :echo]

      ["/genesis" :post create-genesis :route-name :genesis]
      ["/mine" :post [(body-params/body-params) new-block] :route-name :new-block]

      ["/show-chain" :get show-chain :route-name :show-chain]
      ["/show-last-block" :get show-last-block :route-name :show-last-block]
      ["/is-valid" :get is-valid? :route-name :is-valid?]
      }))

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8082
   })

(defonce server (atom nil))

(defn start []
  (http/start (http/create-server service-map)))

(defn start-dev []
  (reset! server
          (http/start
            (http/create-server
              (assoc service-map ::http/join? false)))))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))

(comment
  (start-dev)
  (restart))
