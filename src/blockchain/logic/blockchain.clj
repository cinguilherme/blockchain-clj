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
  (int (* 7876 (Math/random))))

(defn create-genesis-block [{:keys [number data]}]
  (-> {:number        number
       :nonce         (gen-nonce)
       :data          data
       :previous-hash "0000000000000000000000000000000000000000000000000000000000000000"}
      pre-hash->hashed-block))

(defn create-block [previous-block new-block-data]
  (-> {:previous-hash (-> previous-block :hash)
       :number        (-> previous-block :number inc)
       :data          (:data new-block-data)
       :nonce         (gen-nonce)}
      pre-hash->hashed-block))

(defn valid-hash? [block]
  (-> block
      :hash
      (clojure.string/starts-with? "00")))

(defn hash-block [block-data]
  (-> {:previous-hash (-> block-data :hash)
       :number        (-> block-data :number inc)
       :data          (:data block-data)
       :nonce         (gen-nonce)}
      pre-hash->hashed-block))

(defn mine [block-data]
  (loop [block (hash-block block-data)]
    (if (valid-hash? block)
      block
      (recur (hash-block block-data)))))

(comment
  (create-genesis-block {:number 1 :nonce 244 :data {}})
  (create-block (create-genesis-block {:number 1 :nonce 244 :data {}})
                {:data {:important "yeah"}})

  (mine {:previous-hash "000"
         :number 99
         :data {}})
  (gen-nonce))
