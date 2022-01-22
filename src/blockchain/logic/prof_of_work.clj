(ns blockchain.logic.prof-of-work
  (:require [schema.core :as s]
            [buddy.core.hash :as hash]
            [buddy.core.codecs :as codecs]))

(def leading-zeros (str "00"))

(defn hash-prof [prof prev-prof]
  (-> (- (Math/pow prof 2) (Math/pow prev-prof 2))
      str
      hash/sha256
      codecs/bytes->hex))

(defn check-prof [prof prev-prof]
  (-> (hash-prof prof prev-prof)
      (clojure.string/starts-with? leading-zeros)))

(defn gen-prof-of-work [previous-prof]
  (loop [prof 1]
    (if (check-prof prof previous-prof)
      prof
      (recur (inc prof)))))

(defn is-chain-valid? [chain]
  (loop [c chain]
    (if (or (empty? c) (= 1 (count c)))
      true
      (let [block (first c)
            next-block (second c)]
        (cond
          (not (= (:hash block) (:previous-hash next-block)))
          false

          (not (= (hash-prof (:prof next-block) (:prof block))))
          false

          :else
          (recur (rest c)))))))
