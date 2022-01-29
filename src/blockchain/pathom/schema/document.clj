(ns blockchain.pathom.schema.document
  (:require [schema.core :as s]))

(s/defschema Document
  {:document/id s/Num :document/title s/Str})
