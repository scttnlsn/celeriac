(ns celeriac.core-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! timeout]]
            [cljs.test :as test :refer-macros [async deftest is run-tests testing]]
            [celeriac.core :as celeriac]))

(enable-console-print!)

(defn- test-handler [value state]
  (assoc state :value value))

(deftest listen
  (testing "it receives values dispatched to given channel"
    (async done
           (let [dispatcher (celeriac/dispatcher {:test test-handler})
                 ch (celeriac/listen dispatcher :test)]
             (go
               (is (= (<! ch) :foo))
               (done))
             (celeriac/dispatch! dispatcher :test :foo)))))

(deftest listen-all
  (testing "it receives values dispatched to any channel"
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

(deftest start
  (testing "it updates state atom with result of handler"
    (async done
           (let [state (atom {})
                 dispatcher (celeriac/dispatcher {:test test-handler})]
             (go
               (<! (timeout 100))
               (is (= (:value @state) :foo)))
             (celeriac/start! dispatcher state {})
             (celeriac/dispatch! dispatcher :test :foo)))))
