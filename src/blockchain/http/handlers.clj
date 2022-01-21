(ns blockchain.http.handlers
  (:require [blockchain.logic.blockchain :as logic]
            [blockchain.data.state :as state]))


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
