(ns celeriac.routing
  (:require [cljs.core.async :refer [put!]]
            [secretary.core :as secretary])
  (:import goog.History
           goog.history.EventType))

(defn route-handler [name ch]
  (fn [params]
    (put! ch [name params])))

(defn path [routes name & [params]]
  (let [route (get routes name)]
    (secretary/render-route route params)))

(defn routes [ch routes]
  (doseq [[name route] routes]
    (secretary/add-route! route (route-handler name ch)))
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
  (goog.events/listen history
                      EventType/NAVIGATE
                      on-navigate)
  (.setEnabled history true))

(defn redirect! [history path]
  (.replaceToken history path))
