(ns blockchain.logic.hashing
  (:require [buddy.core.hash :as hash]
            [buddy.core.codecs :as codecs]))

(defn ->hashed-block [block]
  (->> block
       str
       hash/sha256
       codecs/bytes->hex
       (assoc block :hash)))

