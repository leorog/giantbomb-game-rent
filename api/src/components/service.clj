(ns components.service
  (:require
   [cheshire.core :as cheshire]
   [components.config :as config]
   [components.db :as db]
   [datahike.api :as d]
   [io.pedestal.http :as http]
   [io.pedestal.http.body-params :as body-params]
   [io.pedestal.http.route :as route]
   [io.pedestal.interceptor :as intc]
   [java-time :as time]
   [rent.interceptor :as rent]
   [search.interceptor :as search]))

(defn routes
  []
  (route/expand-routes
   #{["/v1/rent"
      :post rent/rent-interceptor
      :route-name :rent]
     ["/v1/search"
      :get search/giantbomb-interceptor
      :route-name :search]}))

(defn component-interceptor
  [config]
  (intc/interceptor
   {:name  ::component-interceptor
    :enter (fn [context]
             (let [conn (db/init config)]
               (assoc context
                      :as-of  (time/instant)
                      :conn   conn
                      :db    (d/db conn)
                      :config config)))}))

(def error-interceptor
  (intc/interceptor
   {:name  ::error-interceptor
    :error (fn [context error]
             (assoc context
                    :response
                    {:status 500
                     :body (cheshire/encode {:error (.getMessage error)})}))}) )

(defn create-server
  [{:keys [:port :env] :or {port 3000 env :dev}}]
  (let [config      (config/init env)
        service-map (-> {:env env
                         ::http/routes          (routes)
                         ::http/type            :jetty
                         ::http/join?           (= :prod env)
                         ::http/resource-path   "/public"
                         ::http/allowed-origins (if (= :dev env)
                                                   (constantly true)
                                                   (:cors-config config))
                         ::http/port            port
                         ::http/secure-headers {:content-security-policy-settings nil}}
                        http/default-interceptors
                        (update :io.pedestal.http/interceptors conj
                                http/json-body
                                (body-params/body-params)
                                (component-interceptor config)))]
    (if (= :prod env)
      (http/create-server service-map)
      (http/create-server (http/dev-interceptors service-map)))))

(defn start
  [server]
  (http/start server))

(defn stop
  [server]
  (http/stop server))
