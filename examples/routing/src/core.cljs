(ns examples.routing.core
  (:require [cljs.core.async :refer [<! chan]]
            [secretary.core :as secretary]
            [celeriac.core :as celeriac]
            [celeriac.routing :as routing]))

(enable-console-print!)

(defmulti navigate!
  (fn [[route params] state] route))

(defmethod navigate! :default
  [[name _] state]
  (assoc state :route name))

(defmethod navigate! :baz
  [[name {:keys [id]}] state]
  ;(put! api-ch [:fetch-baz {:id id}])
  (-> state
      (assoc :route name)
      (assoc :baz id)))

(def history (routing/create-history))

(def channels (celeriac/make-channels {:nav navigate!}))

(def state (atom (celeriac/initial-state channels)))

(routing/routes (celeriac/channel channels :nav)
                {:foo "/foo"
                 :bar "/bar"
                 :baz "/baz/:id"})

(secretary/set-config! :prefix "#")
(routing/start-history! history)
(celeriac/start! channels state)
