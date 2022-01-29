(ns blockchain.pathom.logic.person
  (:require [schema.core :as s]
            [blockchain.pathom.schema.person :as b.c.person]))

(defn- map-files-id->file-id [person]
  (mapv (fn [f] {:file/id f}) (:person/files person)))

(s/defn person-files->output :- (s/->Maybe b.c.person/PersonWithFiles) [person]
  (->> (map-files-id->file-id person)
       (assoc person
         :person/files)))
