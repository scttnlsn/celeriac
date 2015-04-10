(ns examples.errors.core
  (:require-macros [celeriac.errors :refer [try+]]
                   [cljs.core.match.macros :refer [match]])
  (:require [cljs.core.match]
            [celeriac.errors :as errors]))

(enable-console-print!)

(errors/maybe-throw "foo")

(try
  (errors/maybe-throw (js/Error.))
  (catch js/Error e
    (println "caught:" e)))

(println (errors/info (errors/make-error {:foo "bar"})))

(try
  (errors/throw+ {:foo "bar"})
  (catch js/Error e
    (println "caught:" (errors/info e))))

(try+
  (errors/throw+ {:foo "bar"})
  (catch e
    (match [e]
      [{:foo "baz"}] (errors/throw+ e)
      [{:foo _}] (println "try+ caught:" e)
      :else (errors/throw+ e))))
