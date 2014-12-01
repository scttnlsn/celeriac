(ns celeriac.routing)

(defmacro defroutes [name ch routes]
  (let [body (for [[route path] routes]
               (let [fn-name (symbol (str (clojure.core/name route) "-path"))]
                 `(defn ~fn-name [& [params#]] (celeriac.routing/path ~routes ~route params#))))]
    `(do
       (def ~name ~routes)
       (celeriac.routing/routes ~ch ~routes)
       ~@body)))
