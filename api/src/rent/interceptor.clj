(ns rent.interceptor
  (:require
   [io.pedestal.interceptor :as intc]
   [rent.db :as rent.db]
   [rent.logic :as rent.logic]))

(def rent-interceptor
  (intc/interceptor
   {:name ::rent
    :enter (fn [context]
             (let [conn (:conn context)
                   params (get-in context [:request :json-params])
                   as-of (:as-of context)
                   tx (rent.logic/rent-tx params as-of)]
               (if-let [error (:error (rent.db/register-rent conn tx))]
                 (assoc context
                        :response {:status 409 :body error})
                 (assoc context
                        :response {:status 201 :body "created"}))))}))
