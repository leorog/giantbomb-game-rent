(ns app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [reagent.core :as r]
   [reagent.dom :as rd]))

(defonce api-url "http://localhost:8080/v1/")

(def initial-state {:games-selected #{}
                    :current-page   :search
                    :games-found    []
                    :last-query     nil
                    :page-id        1})

(def state
  (r/atom initial-state))

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
         (swap! state assoc :games-found body)))) ))

(defn rent!
  [games]
  (go
    (let [{:keys [:success :body]} (<! (http/post (str api-url "rent")
                                                  {:json-params {:game-ids (map :id games)}}))
          games-by-id (group-by :id games)]
      (if success
        (do
          (js/alert "Order Completed! Thank you")
          (reset! state initial-state))
        (do
          (js/alert (str (get-in games-by-id [(:already-rented body) 0 :name]) " is already rented. It will be removed from your selection.\nTry Again"))
          (swap! state assoc :games-selected (flatten (vals (dissoc games-by-id (:already-rented body))))))))))

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
    [:tbody
     (for [{:keys [:id :name :image] :as game} (:games-found @state)]
       ^{:key id}
       [:tr
        [:td [:img {:src (:thumb_url image) :width 200 :height 200}]]
        [:td name]
        [:td [:input {:type :button
                      :value "add to cart"
                      :on-click #(swap! state update :games-selected conj game)}]]]) ]]
   (when (= 10 (count (:games-found @state)))
     [:div.inline-block
      [:input {:type :button
               :disabled (= 1 (:page-id @state))
               :on-click #(get-previous-page)
               :value "previous page"}]
      [:input {:type :button
               :on-click #(get-next-page)
               :value "next page"}]])])

(defn search-page
  []
  [:div
   [:label "game by name or tag:"]
   [:input {:on-input #(swap! state assoc :last-query (-> % .-target .-value))}]
   [:input {:type :button
            :value "search"
            :on-click #(search-games (:last-query @state))}]
   [:div (when (seq (:games-selected @state)) (str "cart: " (map :name (:games-selected @state))))]
   [:div (when (seq (:games-found @state)) (str "found: " (count (:games-found @state))))]

   [game-list-view]
   (when (seq (:games-selected @state))
     [:input {:type :button
              :on-click #(swap! state assoc :current-page :checkout)
              :value "CHECKOUT"}])])

(defn checkout-view
  []
  [:div
   [:h4 "Games you choose:"]
   [:table
    [:tbody
     (for [{:keys [:id :name :image]} (:games-selected @state)]
       ^{:key id}
       [:tr
        [:td [:img {:src (:thumb_url image) :width 200 :height 200}]]
        [:td name]])]]
   [:input {:type :button
            :on-click #(rent! (:games-selected @state))
            :value "RENT"}]
   (when-not (seq (:games-selected @state))
     [:input {:type :button
              :on-click #(reset! state initial-state)
              :value "GO BACK TO SEARCH"}])])

(defn get-view
  [current-view]
  (case current-view
    :search search-page
    :checkout checkout-view))

(defn main-component
  []
  [:div
   [:h1 "GAME SHOP"]
   [(get-view (:current-page @state))]])

(defn mount
  [c]
  (rd/render [c] (.getElementById js/document "root")))

(defn reload!
  []
  (mount main-component))

(defn main!
  []
  (mount main-component))
