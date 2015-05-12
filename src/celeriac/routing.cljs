(ns celeriac.routing
  (:require [cljs.core.async :refer [put!]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [secretary.core :as secretary])
  (:import goog.History))

(defn route-handler [name dispatch-fn]
  (fn [params]
    (dispatch-fn [name params])))

(defn path [routes name & [params]]
  (let [route (get routes name)]
    (secretary/render-route route params)))

(defn routes [dispatch-fn routes]
  (doseq [[name route] routes]
    (secretary/add-route! route (route-handler name dispatch-fn)))
  routes)

(defn strip-slash [s]
  (if (= "/" (last s))
    (clojure.string/replace s #"/$" "")
    s))

(defn create-history []
  (History.))

(defn- on-navigate [e]
  (-> e
      (.-token)
      (strip-slash)
      (secretary/dispatch!)))

(defn start-history! [history]
  (events/listen history
                 EventType/NAVIGATE
                 on-navigate)
  (.setEnabled history true))

(defn redirect! [history path]
  (.replaceToken history path))
