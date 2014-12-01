(ns celeriac.routing
  (:require [cljs.core.async :refer [put!]]
            [secretary.core :as secretary])
  (:import goog.History
           goog.history.EventType))

(defn route-handler [name ch]
  (fn [params]
    (put! ch [name params])))

(defn routes [ch routes]
  (doseq [[name route] routes]
    (secretary/add-route! route (route-handler name ch))))

(defn create-history []
  (History.))

(defn start-history! [history]
  (goog.events/listen history EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
  (.setEnabled history true))
