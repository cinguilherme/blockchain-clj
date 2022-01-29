(ns blockchain.data.tables)

(def people-table
  {1 {:person/id    1 :person/name "Sally"
      :person/age   32 :person/document 1
      :person/files [1]}
   2 {:person/id 2 :person/name "Joe" :person/age 22}
   3 {:person/id 3 :person/name "Fred" :person/age 11}
   4 {:person/id 4 :person/name "Bobby" :person/age 55}})

(def files
  {1 {:file/id 1 :file/title "some" :file/content "thing" :file/author 1}})

(def author-table
  {1 {:author/id 1 :author/name "gui" :author/age 35}})

(def doc-trable
  {1 {:document/id 1 :document/title "TLOTR"}})

(def list-table
  {:friends {:list/id     :friends
             :list/label  "Friends"
             :list/people [1 2]}
   :enemies {:list/id     :enemies
             :list/label  "Enemies"
             :list/people [4 3]}})
