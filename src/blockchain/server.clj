(ns blockchain.server
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))

(defn hello-handler [req]
  {:status 200 :body req})

(def routes
  #{
    ["/hello" :get hello-handler :route-name :hello]
    })

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
  (start-dev))
