(ns user
  (:require
   [components.config :as config]
   [components.db :as db]
   [components.service :as service]
   [datahike.api :as d]))

(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (service/start (service/create-server {}))))

(defn stop-dev []
  (service/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))

(comment
  (def conn (db/init (config/init :prod)))
  (d/q '{:find [[(pull ?e [:*]) ...]]
         :where [[?e :catalog/game-id]]}
       (d/db conn)))
