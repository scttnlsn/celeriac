(ns celeriac.errors)

(defn- parse-catch [form]
  (let [binding (second form)
        body (drop 2 form)]
    `(catch js/Error e#
       (let [~binding (celeriac.errors/info e#)]
         ~@body))))

(defn- parse-catch+ [form]
  (let [binding (second form)
        body (drop 2 form)]
    `(catch js/Error e#
       (let [~binding (celeriac.errors/info e#)]
         (cljs.core.match.macros/match [~binding]
                                       ~@body)))))

(defn- parse-try+ [body]
  (for [form body]
    (condp = (first form)
      'catch (parse-catch form)
      'catch+ (parse-catch+ form)
      form)))

(defmacro try+ [& body]
  `(try
     ~@(parse-try+ body)))
