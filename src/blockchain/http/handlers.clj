(ns blockchain.http.handlers
  (:require [blockchain.logic.blockchain :as logic]
            [blockchain.data.state :as state]
            [blockchain.logic.hashing :as l.hashing]))


(defn hello-handler [req]
  {:status 200 :body req})

(defn create-genesis [req]
  (if (empty? @state/chain)
    (let [genesis (logic/create-genesis-block {:number 1 :data {:init "init"}})
          _ (state/update-chain-with-new-block! genesis)]
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
  (if (empty? @state/chain)
    {:status 400
     :body   "chain is empty, user genesis block endpoint to start the chain"}
    (let [data (-> req :edn-params)
          mined (logic/create-block (state/get-last-block!) data)
          _ (state/update-chain-with-new-block! mined)]
      {:status 201
       :body   mined})))

(defn show-chain [req]
  (let [chain state/chain]
    {:status 200
     :body   @chain}))

(defn is-valid? [req]
  (let [chain (state/deref-chain)
        valid? (blockchain.logic.prof-of-work/is-chain-valid? chain)]
    {:status 200
     :body   {:valid? valid?}}))
