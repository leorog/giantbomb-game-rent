(ns components.config
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]))

(defn from-context
  [{:keys [:config]} path]
  (get config path))

(defn init
  [env]
  (edn/read-string (slurp (io/resource (format "config-%s.edn" (name env))))))
