(ns examples.basic.core
  (:require [celeriac.core :as celeriac]
            [celeriac.dev :refer [repl-connect!]]))

(enable-console-print!)

;; --------------------------------------------------
;; Handler functions
;;
;; - accept current app state and action
;; - return a new app state

(defmulti handler
  (fn [state action]
    (first action)))

(defmethod handler :foo
  [state [_ value]]
  (assoc state :foo value))

(defmethod handler :bar
  [state [_ value]]
  (assoc state :bar value))

;; --------------------------------------------------
;; Middleware functions
;;
;; - accept a handler
;; - return a new handler

(defn logger [handler]
  (fn [state action]
    (println "logger:" [state action])
    (handler state action)))

;; --------------------------------------------------
;; Main

(def store (celeriac/create-store (-> handler
                                      (logger))))

(celeriac/subscribe store
                    (fn [action before after]
                      (println "---")
                      (println "state:" before)
                      (println "action:" action)
                      (println "state:" after)))

(celeriac/dispatch! store [:foo "baz"])
(celeriac/dispatch! store [:bar "qux"])

;; Dev
#_(repl-connect!)
