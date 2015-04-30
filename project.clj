(defproject celeriac "0.1.0-SNAPSHOT"
  :description "Opinionated structure and helpers for ClojureScript/Om applications"
  :dependencies [[org.clojure/clojure "1.7.0-beta1"]
                 [org.clojure/clojurescript "0.0-3211"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [secretary "1.2.3"]]

  :plugins [[lein-cljsbuild "1.0.5"]]

  :profiles {:dev {:dependencies [[com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.nrepl "0.2.10"]
                                  [weasel "0.6.0"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

  :cljsbuild {:builds [{:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "target/test/test.js"
                                   :output-dir "target/test"
                                   :pretty-print true
                                   :source-map true
                                   :optimizations :none}}
                       {:id "example-basic"
                        :source-paths ["src" "examples/basic/src"]
                        :compiler {:output-to "examples/basic/app.js"
                                   :output-dir "examples/basic/out"
                                   :source-map true
                                   :optimizations :none}}
                       {:id "example-routing"
                        :source-paths ["src" "examples/routing/src"]
                        :compiler {:output-to "examples/routing/app.js"
                                   :output-dir "examples/routing/out"
                                   :source-map true
                                   :optimizations :none}}
                       {:id "example-errors"
                        :source-paths ["src" "examples/errors/src"]
                        :compiler {:output-to "examples/errors/app.js"
                                   :output-dir "examples/errors/out"
                                   :source-map true
                                   :optimizations :none}}]})
