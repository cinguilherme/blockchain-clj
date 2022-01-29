(ns blockchain.parser
  (:require [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc :refer [defresolver]]
            [blockchain.data.tables :refer :all]
            [blockchain.pathom.resolvers :refer :all]))

(def resolvers
  [person-resolver
   author-resolver
   file-resolver
   doc-resolver
   list-resolver
   friends-resolver
   enemies-resolver])

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
