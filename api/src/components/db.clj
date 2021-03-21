(ns components.db
  (:require
   [datahike.api :as d]
   [rent.schema :as r.schema]))

(defn schemas
  []
  r.schema/schema)

(defn init
  [{:keys [:db :env]}]
  (let [storage-path (:storage-path db)
        db-config {:initial-tx (schemas)
                   :store (if (= env :prod)
                            {:backend :file :path storage-path}
                            {:backend :mem :id (str (java.util.UUID/randomUUID))})}]
    (if (d/database-exists? db-config)
      (d/connect db-config)
      (do (d/create-database db-config)
          (d/connect db-config)))))
