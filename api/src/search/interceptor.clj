(ns search.interceptor
  (:require
   [components.config :as config]
   [io.pedestal.interceptor :as intc]
   [search.giantbomb-api :as giantbomb-api]))

(def giantbomb-interceptor
  (intc/interceptor
   {:name ::search
    :enter (fn [context]
             (let [giantbomb (config/from-context context :giantbomb)
                   params (get-in context [:request :query-params])]
               (if (:q params)
                 (assoc context
                        :response {:status 200
                                   :body (giantbomb-api/query-games giantbomb params)})
                 (assoc context
                        :response {:status 400
                                   :body {:error "missing `q` query parameter"}}))))}))
