(ns search.interceptor-test
  (:require
   [cheshire.core :as cheshire]
   [clj-http.fake :as fake]
   [clojure.test :refer [deftest testing is]]
   [components.service :as service]
   [io.pedestal.test :as pedestal-test]))

(def service-fn (:io.pedestal.http/service-fn (service/create-server {})))

(defn json-response
  [response]
  (select-keys
   (update response
           :body #(try (cheshire/decode % keyword)
                       (catch Exception _ %)))
   [:status :body]))

(def giantbomb-success-body
  (cheshire/encode
   {:error "OK",
    :limit 10,
    :offset 0,
    :number_of_page_results 5,
    :number_of_total_results 5,
    :status_code 1,
    :results
    [{:id 11339,
      :image {:thumb_url "https://giantbomb1.cbsistatic.com/uploads/scale_avatar/0/6384/629747-counterstrikesourceboxart.jpg"},
      :name "Counter-Strike: Source",
      :resource_type "game"}
     {:id 21302,
      :image {:thumb_url "https://giantbomb1.cbsistatic.com/uploads/scale_avatar/0/3661/1799070-0002865.jpg"},
      :name "S.T.A.L.K.E.R.: Clear Sky",
      :resource_type "game"}],
    :version "1.0"}))

(def giantbomb-success-empty-body
  (cheshire/encode
   {:error "OK",
    :limit 10,
    :offset 0,
    :number_of_page_results 5,
    :number_of_total_results 5,
    :status_code 1,
    :results [],
    :version "1.0"}))

(deftest giant-bombo-interceptor-test
  (testing "it fail if missing required query param"
    (is (= {:status 400,
            :body {:error "missing `q` query parameter"}}
           (json-response (pedestal-test/response-for service-fn :get "/v1/search")))))
  (testing "it query giantbomb api with q param"
    (fake/with-fake-routes-in-isolation
      {#"https://www.giantbomb.com/api/search/.*query=cs.*" (fn [_] {:status 200 :body giantbomb-success-body})}
      (is (= {:status 200,
              :body [{:id 11339,
                      :image {:thumb_url "https://giantbomb1.cbsistatic.com/uploads/scale_avatar/0/6384/629747-counterstrikesourceboxart.jpg"},
                      :name "Counter-Strike: Source",
                      :resource_type "game"}
                     {:id 21302,
                      :image {:thumb_url "https://giantbomb1.cbsistatic.com/uploads/scale_avatar/0/3661/1799070-0002865.jpg"},
                      :name "S.T.A.L.K.E.R.: Clear Sky",
                      :resource_type "game"}]}
             (json-response (pedestal-test/response-for service-fn :get "/v1/search?q=cs"))))))
  (testing "it paginate the results with page param"
    (fake/with-fake-routes-in-isolation
      {#"https://www.giantbomb.com/api/search/.*query=cs&page=1.*" (fn [_] {:status 200 :body giantbomb-success-body})
       #"https://www.giantbomb.com/api/search/.*query=cs&page=2.*" (fn [_] {:status 200 :body giantbomb-success-empty-body})}
      (is (= {:status 200,
              :body [{:id 11339,
                      :image {:thumb_url "https://giantbomb1.cbsistatic.com/uploads/scale_avatar/0/6384/629747-counterstrikesourceboxart.jpg"},
                      :name "Counter-Strike: Source",
                      :resource_type "game"}
                     {:id 21302,
                      :image {:thumb_url "https://giantbomb1.cbsistatic.com/uploads/scale_avatar/0/3661/1799070-0002865.jpg"},
                      :name "S.T.A.L.K.E.R.: Clear Sky",
                      :resource_type "game"}]}
             (json-response (pedestal-test/response-for service-fn :get "/v1/search?q=cs&page=1"))))
      (is (= {:status 200,
              :body []}
             (json-response (pedestal-test/response-for service-fn :get "/v1/search?q=cs&page=2")))))))
