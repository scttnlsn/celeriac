(ns examples.basic.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [celeriac.core :as celeriac]
            [celeriac.dev :refer [repl-connect!]]))

(enable-console-print!)

;; --------------------------------------------------
;; Handler functions
;;
;; - accept current app state and action
;; - return a new app state

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
;; Middleware functions
;;
;; - accept a handler
;; - return a new handler

(defn logger [handler]
  (fn [db action]
    (println "action:" action)
    (handler db action)))

;; --------------------------------------------------
;; Main

(def store (celeriac/create-store (-> handler
                                      (logger))))

(celeriac/subscribe store
                    (fn [db]
                      (println "db:" db)))

(celeriac/dispatch! store [:foo "baz"])
(celeriac/dispatch! store [:bar "qux"])

;; Dev
#_(repl-connect!)
