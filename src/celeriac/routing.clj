(ns celeriac.routing)

(defmacro defroutes [ch routes]
  (let [body (for [[route path] routes]
               (let [fn-name (symbol (str (name route) "-path"))]
                 `(defn ~fn-name [& [params#]] (celeriac.routing/path ~routes ~route params#))))]
    `(do
       (celeriac.routing/routes ~ch ~routes)
       ~@body)))
