(ns blockchain.data.state)

(def chain (atom []))

(defn get-last-block! []
  (let [col @chain]
    (last col)))

(defn update-chain-with-new-block! [new-block]
  (reset! chain (conj @chain new-block)))
