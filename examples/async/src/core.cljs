(ns examples.async.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! timeout]]
            [celeriac.core :as celeriac]
            [celeriac.dev :refer [repl-connect!]]))

(enable-console-print!)

;; --------------------------------------------------
;; Handlers

(defmulti handler
  (fn [db action]
    (first action)))

(defmethod handler :foo
  [db [_ value]]
  (assoc db :foo value))

(defmethod handler :bar
  [db [_ value]]
  (assoc db :bar value))

;; --------------------------------------------------
;; Actions

(defn foo [value]
  [:foo value])

;; Async actions are a function that accepts
;; a dispatch function
(defn bar [value]
  (fn [dispatch]
    (dispatch [:bar value])
    (go
      (<! (timeout 3000))
      (dispatch (foo value)))))

;; --------------------------------------------------
;; Main

(def store (celeriac/create-store handler))

(celeriac/subscribe store
                    (fn [db]
                      (println "db:" db)))

(celeriac/dispatch! store (foo "baz"))
(celeriac/dispatch! store (bar "qux"))

;; Dev
#_(repl-connect!)
