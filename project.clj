(defproject celeriac "0.1.0-SNAPSHOT"
  :description
  "Opinionated structure and helpers for ClojureScript/Om applications"

  :dependencies
  [[org.clojure/clojure "1.7.0-beta2"]
   [org.clojure/clojurescript "0.0-3269"]
   [org.clojure/core.async "0.1.346.0-17112a-alpha"]
   [org.clojure/core.match "0.3.0-alpha4"]
   [secretary "1.2.3"]]

  :plugins
  [[lein-cljsbuild "1.0.5"]]

  :cljsbuild
  {:builds [{:id "test"
             :source-paths ["src" "test"]
             :compiler {:output-to "target/test/test.js"
                        :output-dir "target/test"
                        :pretty-print true
                        :source-map true
                        :optimizations :none}}
            {:id "examples"
             :source-paths ["src"
                            "examples/basic/src"
                            "examples/async/src"
                            "examples/routing/src"]
             :compiler {:output-to "examples/out/examples.js"
                        :output-dir "examples/out"
                        :source-map true
                        :optimizations :none}}]

   :test-commands {"test" ["phantomjs" "test/phantom-test.js"]}})
