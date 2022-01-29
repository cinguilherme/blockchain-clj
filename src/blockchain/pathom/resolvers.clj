(ns blockchain.pathom.resolvers
  (:require [blockchain.data.tables :as t]
            [com.wsscode.pathom.connect :as pc :refer [defresolver]]
            [schema.core :as s]
            [blockchain.pathom.logic.document :as d]
            [blockchain.pathom.logic.person :as b.l.person]))

(defresolver doc-resolver [_ {:person/keys [id]}]
  {::pc/input  #{:person/id}
   ::pc/output [:document/id :document/title]}
  (d/doc-output-resolver t/doc-table id))

(defresolver file-resolver [_ {:file/keys [id]}]
  {::pc/input  #{:file/id}
   ::pc/output [:file/id :file/content :file/title :file/author]}
  (let [file (get t/files id)
        aid (:file/author file)]
    (assoc file :file/author {:author/id aid})))

(defresolver author-resolver [_ {:author/keys [id]}]
  {::pc/input  #{:author/id}
   ::pc/output [:author/name :author/age :author/id]}
  (get t/author-table id))

(defresolver person-resolver [_ {:person/keys [id]}]
  {::pc/input  #{:person/id}
   ::pc/output [:person/name :person/age :person/document {:person/files [:file/id]}]}
  (->> id
       (get t/people-table)
       b.l.person/person-files->output))

(defresolver list-resolver [_ {:list/keys [id]}]
  {::pc/input  #{:list/id}
   ::pc/output [:list/label {:list/people [:person/id]}]}
  (when-let [list (get t/list-table id)]
    (assoc list
      :list/people (mapv (fn [id] {:person/id id}) (:list/people list)))))

(defresolver friends-resolver [_ input]
  {::pc/output [{:friends [:list/id]}]}
  {:friends {:list/id :friends}})

(defresolver enemies-resolver [_ input]
  {::pc/output [{:enemies [:list/id]}]}
  {:enemies {:list/id :enemies}})
