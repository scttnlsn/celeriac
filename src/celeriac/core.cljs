(ns celeriac.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :as async :refer [<! chan put!]]))

(defn dispatch! [{:keys [actions] :as store} action]
  (put! actions action))

(defn dispatch-sync! [{:keys [db handler subscribers] :as store} action]
  (swap! db #(handler % action))
  (doall (for [f @subscribers]
           (f @db))))

(defprotocol IHandleAction
  (-handle-action! [action store]))

(extend-protocol IHandleAction
  function
  (-handle-action! [action store]
    (action #(dispatch! store %)))

  default
  (-handle-action! [action store]
    (dispatch-sync! store action)))

(defn start! [{:keys [actions] :as store}]
  (go-loop []
    (when-let [action (<! actions)]
      (-handle-action! action store)
      (recur)))
  store)

(defn create-store
  ([handler]
   (create-store handler {}))
  ([handler initial-state]
   (let [store {:actions (chan)
                :db (atom initial-state)
                :handler handler
                :subscribers (atom [])}]
     (start! store))))

(defn subscribe [{:keys [subscribers] :as store} f]
  (swap! subscribers conj f))

(defn get-state [{:keys [db] :as store}]
  @db)
