(ns blockchain.logic.blockchain
  (:require [schema.core :as s]
            [buddy.core.hash :as hash]
            [buddy.core.codecs :as codecs]))

(s/defschema Block
  {:number        s/Num
   :nonce         s/Num
   :hash          s/Str
   :previous-hash s/Str
   :data          s/Any
   :timestamp     s/Str
   :prof          s/Num})

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

(defn valid-hash? [block]
  (-> block
      :hash
      (clojure.string/starts-with? "0000")))

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

(defn create-genesis-block [{:keys [number data]}]
  (-> {:number        number
       :nonce         (gen-nonce)
       :data          data
       :previous-hash "0000000000000000000000000000000000000000000000000000000000000000"}
      mine))

(defn create-block [previous-block new-block-data]
  (-> {:previous-hash (-> previous-block :hash)
       :number        (-> previous-block :number inc)
       :data          (:data new-block-data)}
      mine))

(defn hash-prof [prof prev-prof]
  (-> (- (Math/pow prof 2) (Math/pow prev-prof 2))
      str
      hash/sha256
      codecs/bytes->hex))

(defn check-prof [prof prev-prof]
  (-> (hash-prof prof prev-prof)
      (clojure.string/starts-with? "0000")))

(defn prof-of-work [previous-prof]
  (loop [prof 1]
    (if (check-prof prof previous-prof)
      prof
      (recur (inc prof)))))


(defn hash-sequence-valid? [chain]
  (loop [c chain]
    (if (empty? c)
      true
      (let [block (first c)
            nblock (second c)]
        (if (not (= (:hash block) (:previous-hash nblock)))
          false
          (recur (rest c)))))))

(defn is-chain-valid? [chain]
  (loop [c chain]
    (if (empty? c)
      true
      (let [block (first c)
            nblock (second c)]
        (cond
          (not (= (:hash block) (:previous-hash nblock)))
          false

          (not (= (clojure.string/starts-with? "0000"
                                               (hash-prof (:prof block) (:prof nblock)))))
          false

          :else
          (recur (rest c)))))))


(comment
  (create-genesis-block {:number 1 :nonce 244 :data {}})
  (create-block (create-genesis-block {:number 1 :nonce 244 :data {}})
                {:data {:important "yeah"}})

  (mine {:previous-hash "000"
         :number        99
         :data          {}})

  (gen-nonce))
