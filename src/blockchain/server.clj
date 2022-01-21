(ns blockchain.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.content-negotiation :as negotiation]
            [io.pedestal.http.route :as route]
            [blockchain.logic.blockchain :as logic]
            [clojure.data.json :as json]
            [blockchain.data.state :as state]
            [io.pedestal.http.body-params :as body-params]))

(def supported-types ["text/html" "application/edn" "text/plain" "application/json"])

(def content-neg (negotiation/negotiate-content supported-types))

(defn hello-handler [req]
  {:status 200 :body req})

(defn create-genesis [req]
  (if (empty? @state/db)
    (let [genesis (logic/create-genesis-block {:number 1 :data {:init "init"}})
          _ (state/update-db-with-new-block! genesis)]
      {:status 201
       :body   genesis
       })
    {:status 400
     :body   "genesis block was already created."}))

(defn show-last-block [req]
  (let [last (state/get-last-block!)]
    {:status 200
     :body   last}))

(defn new-block [req]
  (if (empty? @state/db)
    {:status 400
     :body   "chain is empty, user genesis block endpoint to start the chain"}
    (let [data (-> req :edn-params)
          mined (logic/create-block (state/get-last-block!) data)
          _ (state/update-db-with-new-block! mined)]
      {:status 201
       :body   mined})))

(defn show-chain [req]
  (let [db state/db]
    {:status 200
     :body   @db}))

(defn ok [body]
  {:status  200 :body body
   :headers {:content-type "application/edn"}})

(def echo
  {:name  ::echo
   :enter (fn [context]
            (let [request (:request context)
                  response (ok request)]
              (assoc context :response response)))})

(def coerce-body
  {:name ::coerce-body
   :leave
   (fn [context]
     (let [accepted (get-in context [:request :accept :field] "text/plain")
           response (get context :response)
           body (get response :body)
           coerced-body (case accepted
                          "text/html" body
                          "text/plain" body
                          "application/edn" (pr-str body)
                          "application/json" (json/write-str body))
           updated-response (assoc response
                              :headers {"Content-Type" accepted}
                              :body coerced-body)]
       (assoc context :response updated-response)))})

(def routes
  (route/expand-routes
    #{
      ["/hello" :get [content-neg coerce-body hello-handler] :route-name :hello]
      ["/hellop" :post [(body-params/body-params) hello-handler] :route-name :hellop]
      ["/genesis" :post create-genesis :route-name :genesis]
      ["/mine" :post [(body-params/body-params) new-block] :route-name :new-block]
      ["/echo" :get echo]
      ["/show-chain" :get show-chain :route-name :show-chain]
      ["/show-last-block" :get show-last-block :route-name :show-last-block]
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
