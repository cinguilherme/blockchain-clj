(ns blockchain.pathom.schema.person
  (:require [schema.core :as s]))

(s/defschema Person
  #:person{:id s/Num
           :name s/Str})

(s/defschema PersonWithFilesAsNum
  (assoc Person :person/files [s/Num]))

(s/defschema PersonWithFiles
  (assoc Person :person/files [{:file/id s/Num}]))

