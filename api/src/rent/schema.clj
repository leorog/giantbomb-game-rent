(ns rent.schema)

(def schema
  [{:db/ident       :catalog/game-id
    :db/valueType   :db.type/long
    :db/unique      :db.unique/identity
    :db/index       true
    :db/cardinality :db.cardinality/one}
   {:db/ident       :rent/rented-at
    :db/valueType   :db.type/instant
    :db/index       true
    :db/cardinality :db.cardinality/one}
   {:db/ident       :rent/returned-at
    :db/valueType   :db.type/instant
    :db/cardinality :db.cardinality/one}])
