(ns blockchain.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.content-negotiation :as negotiation]
            [io.pedestal.http.route :as route]
            [blockchain.logic.blockchain :as logic]
            [clojure.data.json :as json]))

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
   :headers {:content-type "application/edn"}})

(def echo
  {:name ::echo
   :enter (fn [context]
            (let [request (:request context)
                  response (ok request)]
              (assoc context :response response)))})

(def coerce-body
  {:name ::coerce-body
   :leave
   (fn [context]
     (let [accepted         (get-in context [:request :accept :field] "text/plain")
           response         (get context :response)
           body             (get response :body)
           coerced-body     (case accepted
                              "text/html"        body
                              "text/plain"       body
                              "application/edn"  (pr-str body)
                              "application/json" (json/write-str body))
           updated-response (assoc response
                              :headers {"Content-Type" accepted}
                              :body    coerced-body)]
       (assoc context :response updated-response)))})

(def routes
  (route/expand-routes
    #{
      ["/hello" :get [content-neg coerce-body hello-handler] :route-name :hello]
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
