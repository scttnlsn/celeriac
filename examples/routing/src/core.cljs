(ns examples.routing.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<! chan]]
            [secretary.core :as secretary]
            [celeriac.core :as celeriac]
            [celeriac.routing :as routing :refer-macros [defroutes]]
            [celeriac.dev :refer [repl-connect!]]))

(enable-console-print!)

;; --------------------------------------------------
;; Handlers

(defmulti navigate!
  (fn [[route params] state]
    route))

(defmethod navigate! :default
  [[name _] state]
  (assoc state :route name))

(defmethod navigate! :baz
  [[name {:keys [id]}] state]
  (-> state
    (assoc :route name)
    (assoc :baz id)))

;; --------------------------------------------------
;; Globals

(def state (atom {}))

(def dispatcher (celeriac/dispatcher {:nav navigate!}))

(def history (routing/create-history))

;; --------------------------------------------------
;; Routing

(defn dispatch-route! [route]
  (celeriac/dispatch! dispatcher :nav route))

(defroutes routes
  dispatch-route!
  {:home "/"
   :foo "/foo"
   :bar "/bar"
   :baz "/baz/:id"
   :not-found "*"})

(println "paths:"
         (foo-path)
         (bar-path)
         (baz-path {:id 123}))

(secretary/set-config! :prefix "#")

;; Start listening to history events
;; and dispatch a new route each time
;; the URL changes
(routing/start-history! history)

;; --------------------------------------------------
;; Main

;; Listen to all dispatched values
(let [ch (celeriac/listen-all dispatcher)]
  (go-loop []
    (if-let [val (<! ch)]
      (do
        (println "dispatch:" val)
        (recur)))))

;; Start the dispatcher
(celeriac/start! dispatcher
                 state
                 {:dispatcher dispatcher})

;; Dev
#_(repl-connect!)
