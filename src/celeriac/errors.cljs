(ns celeriac.errors)

(defprotocol IError
  (-error? [this]))

(extend-type default
  IError
  (-error? [this] false))

(extend-protocol IError
  js/Error
  (-error? [this] true))

(defn maybe-throw [e]
  (if (-error? e)
    (throw e)
    e))

(defn make-error [info]
  (let [e (js/Error.)]
    (aset e "info" info)
    e))

(defn info [e]
  (aget e "info"))

(defn throw+ [info]
  (throw (make-error info)))
