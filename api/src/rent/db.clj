(ns rent.db
  (:require
   [datahike.api :as d]))

(defn register-rent
  [conn tx]
  (try
    (d/transact conn tx)
    (catch Exception e
      (let [{:keys [:error :old]} (ex-data e)]
        (cond
          (= :transact/cas error) {:error {:already-rented (first old)}}
          :else (throw e))))))
