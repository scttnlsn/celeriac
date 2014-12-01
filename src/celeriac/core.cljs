(ns celeriac.core
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [cljs.core.async :refer [chan]]
            [weasel.repl :as ws-repl]))

(defn log! [title value]
  (.log js/console
        (str "%c " (name title) ":")
        "font-weight: bold"
        (clj->js value)))

(defn- wrap-handler [handler]
  (fn [name value state]
    (log! name value)
    (swap! state (partial handler value))
    (log! :state @state)))

(defn make-channels [handlers]
  (into {} (for [[name handler] handlers]
             [name {:ch (chan)
                    :name name
                    :handler (wrap-handler handler)}])))

(defn channel [channels name]
  (get-in channels [name :ch]))

(defn channel-map [channels]
  (into {} (for [[name {:keys [ch]}] channels]
             [name ch])))

(defn initial-state [channels]
  {:channels (channel-map channels)})

(defn start! [channels state]
  (let [ch-index (into {} (for [[_ {:keys [ch] :as val}] channels]
                            [ch val]))]
    (go
      (while true
        (let [[value ch] (alts! (keys ch-index))
              {:keys [name handler]} (get ch-index ch)]
          (handler name value state))))))

(defn repl-connect! []
  (ws-repl/connect "ws://localhost:9001" :verbose true))
