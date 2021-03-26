(ns rent.logic
  (:require
   [java-time :as time]))

(defn rent-tx
  [{:keys [:game-ids]} as-of]
  (vec
   (mapcat
    (fn [game-id]
      [{:db/id (long game-id)
        :catalog/game-id (long game-id)}
       [:db/cas [:catalog/game-id (long game-id)] :rent/rented-at nil (time/to-java-date as-of)]])
    game-ids)))
