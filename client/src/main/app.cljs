(ns app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [reagent.core :as r]
   [reagent.dom :as rd]))

(defonce api-url "http://localhost:8080/v1/")

(def state
  (r/atom {:selected #{}
           :games-found []
           :last-query nil
           :page-id 1}))

(defn search-games
  ([query]
   (swap! state assoc :page-id 1)
   (search-games query 1))
  ([query page]
   (go
     (let [{:keys [:success :body]} (<! (http/get (str api-url "search")
                                                  {:query-params {"q" query
                                                                  "page" page}}))]
       (when success
         (reset! state (assoc @state :games-found body))))) ))

(defn get-next-page
  []
  (swap! state update :page-id inc)
  (search-games (:last-query @state) (:page-id @state)))

(defn get-previous-page
  []
  (swap! state update :page-id dec)
  (search-games (:last-query @state) (:page-id @state)))

(defn game-list-view
  []
  [:div.inline-block
   [:table
    (for [{:keys [:id :name :image]} (:games-found @state)]
      [:tr
       [:td [:img {:src (:thumb_url image) :width 200 :height 200}]]
       [:td name]
       [:td [:input {:type :button
                     :value "add to cart"
                     :on-click #(swap! state update :selected conj id)}]]])]
   (when (= 10 (count (:games-found @state)))
     [:div.inline-block
      [:input {:type :button
               :disabled (= 1 (:page-id @state))
               :on-click #(get-previous-page)
               :value "previous page"}]
      [:input {:type :button
               :on-click #(get-next-page)
               :value "next page"}]])])

(defn main-component
  []
  [:div
   [:h1 "GAME SHOP"]
   [:div
    [:label "game by name or tag:"]
    [:input {:on-input #(swap! state assoc :last-query (-> % .-target .-value))}]
    [:input {:type :button
             :value "search"
             :on-click #(search-games (:last-query @state))}]
    [:div (when (seq (:selected @state)) (str "cart: " (:selected @state)))]
    [:div (when (seq (:games-found @state)) (str "found: " (count (:games-found @state))))]

    [game-list-view]
    (when (seq (:selected @state))
      [:input {:type :button
               :value "CHECKOUT"}])]

   [:div.w-full
    [:div.m-10 {:id "chart"}]]])

(defn mount
  [c]
  (rd/render [c] (.getElementById js/document "root")))

(defn reload!
  []
  (mount main-component))

(defn main!
  []
  (mount main-component))
