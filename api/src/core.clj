(ns core
  (:gen-class)
  (:require
   [components.service :as service]))

(defn -main
  []
  (service/start (service/create-server {:port 8080 :env :prod})))
