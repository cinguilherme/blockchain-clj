(defproject blockchain "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/data.json "2.4.0"]
                 [org.slf4j/slf4j-simple "1.7.28"]

                 [io.pedestal/pedestal.service "0.5.10"]
                 [io.pedestal/pedestal.service-tools "0.5.10"] ;; Only needed for ns-watching; WAR tooling
                 [io.pedestal/pedestal.jetty "0.5.10"]
                 [io.pedestal/pedestal.immutant "0.5.10"]
                 [io.pedestal/pedestal.log "0.5.10"]        ;; Logging and runtime metrics
                 [io.pedestal/pedestal.interceptor "0.5.10"] ;; The Interceptor chain and the Interceptor API
                 [io.pedestal/pedestal.route "0.5.10"]      ;; Efficient routing algorithms and data structures

                 [lafuente/pathom-pedestal "0.1.7"]
                 [com.wsscode/pathom "2.4.0"]

                 [buddy/buddy-core "1.10.413"]

                 [prismatic/schema "1.2.0"]

                 [clj-kondo "2022.01.15"]]
  :repl-options {:init-ns blockchain.server})
