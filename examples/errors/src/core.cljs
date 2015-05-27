(ns examples.errors.core
  (:require [cljs.core.match :refer-macros [match]]
            [celeriac.errors :refer [info make-error maybe-throw throw+] :refer-macros [try+]]
            [celeriac.dev :refer [repl-connect!]]))

(enable-console-print!)

;; Doesn't throw if the given argument is
;; not an error
(maybe-throw "foo")

;; Throw and catch a basic error
(try
  (maybe-throw (js/Error.))
  (catch js/Error e
    (println "caught:" e)))

;; Make an error with metadata
;; Extract the metadata from the rror
(println (info (make-error {:foo "bar"})))

;; Make and throw an error with the given metadata
;; Extract the metadata once caught
(try
  (throw+ {:foo "bar"})
  (catch js/Error e
    (println "caught:" (info e))))

;; Throw an error with the given metadata
;; Match on the metadata once caught
;; `e` bound to error metadata
(try+
 (throw+ {:foo "bar"})
 (catch e
     (match [e]
            [{:foo "baz"}] (throw+ e)
            [{:foo _}] (println "try+/catch caught:" e)
            :else (throw+ e))))

;; Throw an error with the given metadata
;; Automatically match the metadata once caught
;; `e` bound to error metadata
(try+
 (throw+ {:foo "bar"})
 (catch+ e
         [{:foo "baz"}] (throw+ e)
         [{:foo _}] (println "try+/catch+ caught:" e)
         :else (throw+ e)))

#_(repl-connect!)
