(ns celeriac.errors)

(defn- parse-try+ [body]
  (for [form body]
    (if (= (first form) 'catch)
      (let [name (second form)
            body (drop 2 form)]
        `(catch js/Error e#
           (let [~name (celeriac.errors/info e#)]
             ~@body)))
      form)))

(defmacro try+ [& body]
  `(try
     ~@(parse-try+ body)))
