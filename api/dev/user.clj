(ns user
  (:require
   [components.service :as service]))

(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (service/start (service/create-server {}))))

(defn stop-dev []
  (service/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))
