(ns blockchain.logic.blockchain
  (:require [schema.core :as s]
            [buddy.core.hash :as hash]
            [buddy.core.codecs :as codecs]))

(s/defschema Block
  {:number        s/Num
   :nonce         s/Num
   :hash          s/Str
   :previous-hash s/Str
   :data          s/Any})

(s/defschema PreHash
  (dissoc Block :hash))

(s/set-fn-validation! true)

(defn pre-hash->hashed-block [pre-hash]
  (->> pre-hash
       str
       hash/sha256
       codecs/bytes->hex
       (assoc pre-hash :hash)))

(defn- gen-nonce []
  (int (* 76 (Math/random))))

(defn create-genesis-block [{:keys [number nonce data]}]
  (let [pre-hashed {:number        number
                    :nonce         nonce
                    :data          data
                    :previous-hash "0000000000000000000000000000000000000000000000000000000000000000"}]
    (pre-hash->hashed-block pre-hashed)))

(defn create-block [previous-block new-block-data]
  (-> {:previous-hash (-> previous-block :hash)
       :number        (-> previous-block :number inc)
       :data          (:data new-block-data)
       :nonce         (gen-nonce)}
      pre-hash->hashed-block))

(comment
  (create-genesis-block {:number 1 :nonce 244 :data {}})
  (create-block (create-genesis-block {:number 1 :nonce 244 :data {}})
                {:data {:important "yeah"}})
  (gen-nonce))
