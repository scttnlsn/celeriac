(ns celeriac.dev
  (:require [cemerick.piggieback :as piggieback]
            [weasel.repl.websocket :as weasel]))

(defn browser-repl []
  (piggieback/cljs-repl
   (weasel/repl-env :ip "0.0.0.0" :port 9001)))

(comment (celeriac.dev/browser-repl))
