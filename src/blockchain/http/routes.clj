(ns blockchain.http.routes
  (:require [io.pedestal.http.route :as route]
            [blockchain.http.interceptors :as bb.interceptors]
            [io.pedestal.http.body-params :as body-params]
            [blockchain.http.handlers :as h]
            [blockchain.parser :refer [api-handler]]
            [io.pedestal.http.content-negotiation :as negotiation]))

(def supported-types ["text/html" "application/edn" "text/plain" "application/json"])
(def content-neg (negotiation/negotiate-content supported-types))

(def routes
  (route/expand-routes
    #{["/hello" :get [content-neg bb.interceptors/coerce-body h/hello-handler] :route-name :hello]
      ["/hellop" :post [(body-params/body-params) h/hello-handler] :route-name :hellop]
      ["/echo" :get [bb.interceptors/echo] :route-name :echo]

      ["/genesis" :post h/create-genesis :route-name :genesis]
      ["/mine" :post [(body-params/body-params) h/new-block] :route-name :new-block]

      ["/api" :post [(body-params/body-params) api-handler] :route-name :api]

      ["/show-chain" :get h/show-chain :route-name :show-chain]
      ["/show-last-block" :get h/show-last-block :route-name :show-last-block]
      ["/is-valid" :get h/is-valid? :route-name :is-valid?]}))
