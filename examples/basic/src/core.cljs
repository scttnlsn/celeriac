(ns examples.basic.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [celeriac.core :as celeriac]))

(enable-console-print!)

;; --------------------------------------------------
;; Handlers:
;; - accept a dispatched value and the current app state
;; - optional accept map of shared data
;; - return a new app state
;; - may have other side effects (such as dispatching a new value)

(defn foo [value state {:keys [dispatcher]}]
  (celeriac/dispatch! dispatcher :bar "quux")
  (assoc state :foo value))

(defn bar [value state]
  (assoc state :bar value))

;; --------------------------------------------------
;; Globals

(def state (atom {}))

(def dispatcher (celeriac/dispatcher {:foo foo
                                      :bar bar}))

;; --------------------------------------------------
;; Main

;; Listen to all dispatched values
(let [ch (celeriac/listen-all dispatcher)]
  (go-loop []
    (if-let [val (<! ch)]
      (do
        (println "dispatch:" val)
        (recur)))))

;; Start dispatcher
(celeriac/start! dispatcher
                 state
                 {:dispatcher dispatcher})

;; Dispatch some values
;; - corresponding handlers will be called async
(celeriac/dispatch! dispatcher :foo "baz")
(celeriac/dispatch! dispatcher :bar "qux")

;; Dev
#_(celeriac/repl-connect!)
