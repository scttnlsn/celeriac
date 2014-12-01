(ns examples.routing.core
  (:require-macros [celeriac.routing :refer [defroutes]])
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

(defroutes routes
  (celeriac/channel channels :nav)
  {:foo "/foo"
   :bar "/bar"
   :baz "/baz/:id"})

(println "paths:"
         (foo-path)
         (bar-path)
         (baz-path {:id 123}))

(routing/redirect! history (foo-path))

(secretary/set-config! :prefix "#")
(routing/start-history! history)
(celeriac/start! channels state)

(celeriac/repl-connect!)
