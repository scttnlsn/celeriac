(ns celeriac.core
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require [cljs.core.async :as async :refer [chan merge mult pipe put! tap]]))

(defn- wrap-handler [handler]
  (fn [name value state opts]
    (swap! state #(handler value % opts))))

(defn dispatcher [handlers]
  (into {} (for [[name handler] handlers]
             (let [ch (chan)]
               [name {:ch ch
                      :mult-ch (mult ch)
                      :name name
                      :handler (wrap-handler handler)}]))))

(defn channel [dispatcher name]
  (get-in dispatcher [name :ch]))

(defn tap-channels [dispatcher]
  (into {} (for [[_ {:keys [mult-ch] :as item}] dispatcher]
             (let [tapped-ch (chan)]
               (tap mult-ch tapped-ch)
               [tapped-ch item]))))

(defn start! [dispatcher state opts]
  (let [channels (tap-channels dispatcher)]
    (go
      (while true
        (let [[value ch] (alts! (keys channels))
              {:keys [name handler]} (get channels ch)]
          (handler name value state opts))))))

(defn dispatch! [dispatcher name value]
  (let [ch (get-in dispatcher [name :ch])]
    (put! ch value)))

(defn listen [dispatcher name]
  (let [mult-ch (get-in dispatcher [name :mult-ch])
        ch (chan)]
    (tap mult-ch ch)
    ch))

(defn listen-all [dispatcher]
  (let [channels (for [name (keys dispatcher)]
                   (pipe (listen dispatcher name)
                         (chan 1 (map (fn [val] [name val])))))]
    (merge (doall (reverse channels)))))
