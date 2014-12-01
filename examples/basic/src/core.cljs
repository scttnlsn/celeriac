(ns examples.basic.core
  (:require [cljs.core.async :refer [put!]]
            [celeriac.core :as celeriac]))

(enable-console-print!)

; Channel handlers:
; - accept a value from a channel and the current app state
; - return a new app state
; - may have other side effects

(defn foo [value state]
  (let [bar-ch (get-in state [:channels :bar])]
    (put! bar-ch "quux")
    (assoc state :foo value)))

(defn bar [value state]
  (assoc state :bar value))

; main

(def channels (celeriac/make-channels {:foo foo
                                       :bar bar}))

(def state (atom (celeriac/initial-state channels)))

(celeriac/start! channels state)

; Invoking handlers:
; - values are read off control channels
; - corresponding handlers are called
; - app state is swapped w/ new state returned from handler
(put! (celeriac/channel channels :foo) "baz")
(put! (celeriac/channel channels :bar) "qux")

(celeriac/repl-connect!)
