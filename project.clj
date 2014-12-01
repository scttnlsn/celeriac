(defproject celeriac "0.1.0-SNAPSHOT"
  :description "Opinionated structure and helpers for ClojureScript/Om applications"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.cemerick/piggieback "0.1.3"]
                 [weasel "0.4.2"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :cljsbuild {:builds [{:id "basic"
                        :source-paths ["src" "examples/basic/src"]
                        :compiler {:output-to "examples/basic/app.js"
                                   :output-dir "examples/basic/out"
                                   :source-map true
                                   :optimizations :none}}]})
