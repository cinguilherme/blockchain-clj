(ns blockchain.pathom.resolvers
  (:require [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc :refer [defresolver]]
            [blockchain.data.tables :refer :all]))

(defresolver doc-resolver [env {:person/keys [id]}]
             {::pc/input  #{:person/id}
              ::pc/output [:document/id :document/title]}
             (get doc-trable id))

(defresolver file-resolver [env {:file/keys [id]}]
             {::pc/input  #{:file/id}
              ::pc/output [:file/id :file/content :file/title :file/author]}
             (let [filex (get files id)
                   aid (:file/author filex)]
               (assoc filex :file/author {:author/id aid})))

(defresolver author-resolver [env {:author/keys [id]}]
             {::pc/input  #{:author/id}
              ::pc/output [:author/name :author/age :author/id]}
             (get author-table id))

(defresolver person-resolver [env {:person/keys [id]}]
             {::pc/input  #{:person/id}
              ::pc/output [:person/name :person/age :person/document {:person/files [:file/id]}]}
             (let [person (get people-table id)]
               (assoc person :person/files (mapv (fn [f] {:file/id f}) (:person/files person)))))

(defresolver list-resolver [env {:list/keys [id]}]
             {::pc/input  #{:list/id}
              ::pc/output [:list/label {:list/people [:person/id]}]}
             (when-let [list (get list-table id)]
               (assoc list
                 :list/people (mapv (fn [id] {:person/id id}) (:list/people list)))))

(defresolver friends-resolver [env input]
             {::pc/output [{:friends [:list/id]}]}
             {:friends {:list/id :friends}})

(defresolver enemies-resolver [env input]
             {::pc/output [{:enemies [:list/id]}]}
             {:enemies {:list/id :enemies}})
