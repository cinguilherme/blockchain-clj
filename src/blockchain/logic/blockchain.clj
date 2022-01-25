(ns blockchain.logic.blockchain
  (:require [blockchain.logic.hashing :refer :all]
            [blockchain.logic.prof-of-work :refer :all]))

(defn create-genesis-block [{:keys [number data]}]
  (-> {:number        number
       :prof          1
       :data          data
       :previous-hash "0000000000000000000000000000000000000000000000000000000000000000"}
      ->hashed-block))

(defn create-block [previous-block new-block-data]
  (let [prof (gen-prof-of-work (:prof previous-block))]
    (-> {:previous-hash (-> previous-block :hash)
         :number        (-> previous-block :number inc)
         :data          (:data new-block-data)
         :prof          prof}
        ->hashed-block)))


;; Repl tests
(comment
  (create-genesis-block {:number 1 :nonce 244 :data {}})
  (create-block (create-genesis-block {:number 1 :nonce 244 :data {}})
                {:data {:important "yeah"}})
  )
