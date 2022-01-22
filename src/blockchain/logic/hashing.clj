(ns blockchain.logic.hashing
  (:require [schema.core :as s]
            [buddy.core.hash :as hash]
            [buddy.core.codecs :as codecs]
            [blockchain.logic.prof-of-work :as pow]))

(defn ->hashed-block [block]
  (->> block
       str
       hash/sha256
       codecs/bytes->hex
       (assoc block :hash)))

