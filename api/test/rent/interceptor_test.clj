(ns rent.interceptor-test
  (:require
   [cheshire.core :as cheshire]
   [clojure.test :refer [deftest testing is]]
   [components.service :as service]
   [io.pedestal.test :as pedestal-test]))

(def service-fn (:io.pedestal.http/service-fn (service/create-server {})))

(deftest rent-interceptor-test
  (testing "it should return 201 created on success"
    (is (= {:status 201,
            :body "created"}
           (select-keys (pedestal-test/response-for service-fn
                                        :post "/v1/rent"
                                        :headers {"Content-Type" "application/json"}
                                        :body (cheshire/encode {:game-ids [1 2 3]}))
                        [:status :body])))))
