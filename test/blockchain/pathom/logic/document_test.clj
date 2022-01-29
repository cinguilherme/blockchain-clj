(ns blockchain.pathom.logic.document-test
  (:require [clojure.test :refer :all])
  (:require [blockchain.pathom.logic.document :refer [doc-output-resolver]]))

(deftest doc-output-resolver-test
  (is (= {:document/id 1 :document/title "test"}
         (doc-output-resolver {1 {:document/id 1 :document/title "test"}} 1))))
