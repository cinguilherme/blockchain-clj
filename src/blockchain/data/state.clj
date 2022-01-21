(ns blockchain.data.state)

(def db (atom []))

(defn get-last-block! []
  (let [col @db]
    (last col)))

(defn update-db-with-new-block! [new-block]
  (reset! db (conj @db new-block)))
