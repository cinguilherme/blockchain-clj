(ns blockchain.http.interceptors
  (:require [clojure.data.json :as json]))


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
