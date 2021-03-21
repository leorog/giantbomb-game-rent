(ns rent.db-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [components.db :as db]
   [rent.db :as rent.db]
   [rent.logic :as rent.logic]))

(deftest register-rent-test
  (let [conn (db/init {:env :test})
        base-state (rent.logic/rent-tx {:game-ids [1]} #inst "2021-03-21")
        _ (rent.db/register-rent conn base-state)]
    (testing "it handle cas exceptions"
      (is (= {:error {:already-rented 4}}
             (rent.db/register-rent conn base-state))))))
