(defproject datomic-examples "0.1.0-SNAPSHOT"
  :description "Datomic query examples"

  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.datomic/datomic-pro "1.0.6222"]]

  :profiles {:uberjar {:aot :all}})
