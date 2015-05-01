(ns celeriac.errors-test
  (:require [cljs.test :as test :refer-macros [async deftest is run-tests testing]]
            [celeriac.errors :refer [throw+] :refer-macros [try+]]))

(deftest errors
  (testing "it catches based on metadata"
    (try+
     (throw+ {:foo "bar"})
     (catch+ e
             [{:foo _}] ()
             :else (throw+ e))))
  (testing "it throws when metadata does not match"
    (is (thrown? js/Error (try+
                           (throw+ {:foo "bar"})
                           (catch+ e
                                   [{:foo "baz"}] ()
                                   :else (throw+ e)))))))

(run-tests)
