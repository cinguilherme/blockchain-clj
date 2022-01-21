(ns blockchain.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.content-negotiation :as negotiation]
            [io.pedestal.http.route :as route]
            [blockchain.logic.blockchain :as logic]))

(def supported-types ["text/html" "application/edn" "text/plain" "application/json"])

(def content-neg (negotiation/negotiate-content supported-types))

(defn hello-handler [req]
  {:status 200 :body req})

(defn create-genesis [req]
  (let [genesis (logic/create-genesis-block {:number 1 :data {:init "init"}})]
    {:status 201
     :body   genesis
     }))

(defn ok [body]
  {:status  200 :body body
   :headers {:content-type "text/html"}})

(def echo
  {:name ::echo
   :enter (fn [context]
            (let [request (:request context)
                  response (ok request)]
              (assoc context :response response)))})

(def routes
  (route/expand-routes
    #{
      ["/hello" :get [content-neg hello-handler] :route-name :hello]
      ["/hellop" :post hello-handler :route-name :hellop]
      ["/genesis" :post create-genesis :route-name :genesis]
      ["/echo" :get echo]
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
