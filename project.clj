(defproject s3-index "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [amazonica "0.3.52"]
                 [hiccup "1.0.5"]]
  :main ^:skip-aot s3-index.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
