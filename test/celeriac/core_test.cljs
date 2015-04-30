(ns celeriac.core-test
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [cljs.test :refer [async deftest is run-tests testing]])
  (:require [cljs.core.async :refer [<! timeout]]
            [cljs.test :as test]
            [celeriac.core :as celeriac]))

(enable-console-print!)

(defn- test-handler [value state]
  (assoc state :value value))

(deftest dispatcher
  (testing "it dispatches values from single channel to listeners"
    (async done
           (let [dispatcher (celeriac/dispatcher {:test test-handler})
                 ch (celeriac/listen dispatcher :test)]
             (go
               (is (= (<! ch) :foo))
               (done))
             (celeriac/dispatch! dispatcher :test :foo))))
  (testing "it dispatches value from all channels to listeners"
    (async done
           (let [dispatcher (celeriac/dispatcher {:a test-handler
                                                  :b test-handler})
                 ch (celeriac/listen-all dispatcher)]
             (go
               (is (= (<! ch) [:a :foo]))
               (is (= (<! ch) [:b :bar]))
               (done))
             (celeriac/dispatch! dispatcher :a :foo)
             (celeriac/dispatch! dispatcher :b :bar)))))

(deftest handler
  (testing "it updates state atom"
    (async done
           (let [state (atom {})
                 dispatcher (celeriac/dispatcher {:test #(assoc %2 :value %1)})]
             (go
               (<! (timeout 100))
               (is (= (:value @state) :foo)))
             (celeriac/start! dispatcher state {})
             (celeriac/dispatch! dispatcher :test :foo)))))

(run-tests)
