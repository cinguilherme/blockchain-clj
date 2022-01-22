(ns blockchain.schemas.block
  (:require [schema.core :as s]))

(s/defschema Block
             {:number        s/Num
              :nonce         s/Num
              :hash          s/Str
              :previous-hash s/Str
              :data          s/Any
              :timestamp     s/Str
              :prof          s/Num})
