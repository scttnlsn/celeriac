(ns examples.routing.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! chan]]
            [secretary.core :as secretary]
            [celeriac.routing :as routing]))

(enable-console-print!)

(def nav-ch (chan))

(def history (routing/create-history))

(routing/routes nav-ch {:foo "/foo"
                        :bar "/bar"
                        :baz "/baz/:id"})

(secretary/set-config! :prefix "#")
(routing/start-history! history)

(go
  (while true
    (println (<! nav-ch))))
