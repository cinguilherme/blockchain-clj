(ns blockchain.parser
  (:require [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc :refer [defresolver]]))

(def people-table
  {1 {:person/id 1 :person/name "Sally"
      :person/age 32 :person/document 1
      :person/files [1]}
   2 {:person/id 2 :person/name "Joe" :person/age 22}
   3 {:person/id 3 :person/name "Fred" :person/age 11}
   4 {:person/id 4 :person/name "Bobby" :person/age 55}})

(def files
  {1 {:file/id 1 :file/title "some" :file/content "thing"}})

(def doc-trable
  {1 {:document/id 1 :document/title "TLOTR"}})

(def list-table
  {:friends {:list/id     :friends
             :list/label  "Friends"
             :list/people [1 2]}
   :enemies {:list/id     :enemies
             :list/label  "Enemies"
             :list/people [4 3]}})

(defresolver doc-resolver [env {:person/keys [id]}]
  {::pc/input  #{:person/id}
   ::pc/output [:document/id :document/title]}
  (get doc-trable id))

(defresolver file-resolver [env {:file/keys [id]}]
  {::pc/input #{:file/id}
   ::pc/output [:file/id :file/content :file/title]}
  (get files id))

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

(def resolvers [person-resolver file-resolver doc-resolver list-resolver friends-resolver enemies-resolver])

(def pathom-parser
  (p/parser {::p/env     {::p/reader                 [p/map-reader
                                                      pc/reader2
                                                      pc/ident-reader
                                                      pc/index-reader]
                          ::pc/mutation-join-globals [:tempids]}
             ::p/mutate  pc/mutate
             ::p/plugins [(pc/connect-plugin {::pc/register resolvers})
                          p/error-handler-plugin
                          ;; or p/elide-special-outputs-plugin
                          (p/post-process-parser-plugin p/elide-not-found)]}))

(defn api-parser [query]
  (pathom-parser {} query))

(defn api-handler [req]
  (let [q (-> req :edn-params :query)
        par (api-parser q)]
    (do (println par)
        {:status 200
         :body   par})))
