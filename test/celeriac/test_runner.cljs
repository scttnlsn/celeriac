(ns celeriac.test-runner
  (:require [cljs.test :as test :refer [report] :refer-macros [run-tests]]
            [celeriac.core-test]
            [celeriac.errors-test]
            [celeriac.routing-test]))

(enable-console-print!)

(def callbacks (atom []))

(defn register-callback [f]
  (swap! callbacks conj f))

(defmethod report [::test/default :summary] [m]
  (println "\nRan" (:test m) "tests containing"
           (+ (:pass m) (:fail m) (:error m)) "assertions.")

  (println (:fail m) "failures," (:error m) "errors.")

  (let [failures (+ (:fail m) (:error m))]
    (doall
     (for [f @callbacks]
       (f failures)))))

(defn run []
  (test/run-tests
   (test/empty-env ::test/default)
   'celeriac.core-test
   'celeriac.errors-test
   'celeriac.routing-test))
