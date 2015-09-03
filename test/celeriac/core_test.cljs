(ns celeriac.core-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! timeout]]
            [cljs.test :as test :refer-macros [async deftest is testing]]
            [celeriac.core :as celeriac]))

(enable-console-print!)

(defn- test-handler [db action]
  (assoc db :foo (:foo action)))

(deftest test-store
  (testing "it dispatches and handles actions"
    (async done
           (let [store (celeriac/create-store test-handler)]
             (celeriac/subscribe store (fn [db]
                                         (is (= db {:foo :bar}))
                                         (done)))
             (celeriac/dispatch! store {:foo :bar}))))
  (testing "it handles async actions"
    (async done
           (let [store (celeriac/create-store test-handler)]
             (celeriac/subscribe store (fn [db]
                                         (is (= db {:foo :bar}))
                                         (done)))
             (celeriac/dispatch! store (fn [dispatch]
                                         (go
                                           (<! (timeout 10))
                                           (dispatch {:foo :bar}))))))))

(deftest test-store-initial-state
  (testing "it initializes db"
    (let [store (celeriac/create-store test-handler {:foo :bar})]
      (is (= (celeriac/get-state store) {:foo :bar})))))

(defn- test-middleware [handler]
  (fn [db action]
    (let [result (handler db action)]
      (assoc result :middleware true))))

(deftest test-store-middleware
  (testing "it passes db and action through middleware"
    (async done
           (let [store (celeriac/create-store (-> test-handler
                                                  (test-middleware)))]
             (celeriac/subscribe store (fn [db]
                                         (is (= db {:middleware true
                                                    :foo :bar}))
                                         (done)))
             (celeriac/dispatch! store {:foo :bar})))))
