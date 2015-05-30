(ns celeriac.dev
  (:require [cljs.repl :as repl]
            [cljs.repl.browser :as browser]))

(defn browser-repl []
  (repl/repl (browser/repl-env)))

(comment
  (browser-repl)
  )
