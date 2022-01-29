(ns blockchain.pathom.logic.document
  (:require [schema.core :as s]
            [blockchain.pathom.schema.document :refer [Document]]))

(s/defn doc-output-resolver :- Document
  [table :- s/Any
   id :- s/Num]
  (get table id))
