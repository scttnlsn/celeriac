(ns examples.routing.core
  (:require[goog.events :as events]
           [goog.history.EventType :as EventType]
           [secretary.core :as secretary :refer-macros [defroute]]
           [celeriac.core :as celeriac]
           [celeriac.dev :refer [repl-connect!]])
  (:import goog.History))

(enable-console-print!)

;; --------------------------------------------------
;; Handlers

(defmulti handler
  (fn [state action]
    (first action)))

(defmethod handler :navigate
  [state [_ name params]]
  (assoc state :route {:name name
                       :params params}))

;; --------------------------------------------------
;; Routing

(declare store)

(secretary/set-config! :prefix "#")

(def routes
  {:home "/"
   :foo "/foo"
   :bar "/bar"
   :baz "/baz/:id"})

(defn install-routes [store]
  (doseq [[name path] (into [] routes)]
    (secretary/add-route! path #(celeriac/dispatch! store [:navigate name %]))))

(defn start-router []
  (let [history (History.)]
    (goog.events/listen history
                        EventType/NAVIGATE
                        #(secretary/dispatch! (.-token %)))
    (.setEnabled history true)
    history))

;; --------------------------------------------------
;; Main

(def store (celeriac/create-store handler))

(celeriac/subscribe store #(println "route:" (:route %3)))

(install-routes store)
(start-router)

;; Dev
#_(repl-connect!)
