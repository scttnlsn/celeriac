(ns celeriac.routing-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! timeout]]
            [cljs.test :as test :refer-macros [async deftest is run-tests testing]]
            [celeriac.core :as celeriac]
            [celeriac.routing :as routing :refer-macros [defroutes]]))

(enable-console-print!)

(def state (atom {}))
(def dispatcher (celeriac/dispatcher {:nav #(assoc %2 :route %1)}))
(def history (routing/create-history))

(defroutes routes
  #(celeriac/dispatch! dispatcher :nav %)
  {:home "/"
   :foo "/foo"
   :bar "/bar/:id"
   :not-found "*"})

(routing/start-history! history)
(celeriac/start! dispatcher state {})

(deftest routing
  (testing "it updates state when URL changes"
    (async done
           (go
             (<! (timeout 100))
             (is (= (:route @state) [:home {}]))

             (routing/redirect! history (foo-path))
             (<! (timeout 100))
             (is (= (:route @state) [:foo {}]))

             (routing/redirect! history (bar-path {:id 123}))
             (<! (timeout 100))
             (is (= (:route @state) [:bar {:id "123"}]))

             (routing/redirect! history "/wrong")
             (<! (timeout 100))
             (is (= (:route @state) [:not-found {:* "/wrong"}]))

             (routing/redirect! history (home-path))
             (done)))))

(run-tests)
