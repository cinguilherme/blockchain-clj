(ns blockchain.parser-test
  (:require [clojure.test :refer :all])
  (:require [blockchain.parser :refer [api-handler api-parser doc-resolver doc-trable people-table]]))

(deftest api-handler-test
  (testing "how do I test a resolver?"
    (with-redefs [doc-trable {1 {:document/id 1 :document/title "test"}}
                  people-table {1 {:person/id 1 :document/id 1 :person/name "Sammy" :person/age 21}}]
      (is (= {[:person/id 1] {:person/name "Sammy" :document/title "test"}}
             (api-parser [{[:person/id 1] [:person/name
                                           :document/title]}]))))))
