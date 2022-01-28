(ns blockchain.parser-test
  (:require [clojure.test :refer :all])
  (:require [blockchain.parser :refer [api-handler api-parser doc-resolver]]))

(deftest api-handler-test
  (testing "how do I test a resolver?"
    (is (= {[:person/id 1] {:person/name "Sally" :document/title "TLOTR"}}
           (api-parser [{[:person/id 1] [:person/name
                                         :document/title]}])))))
