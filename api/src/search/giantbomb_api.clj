(ns search.giantbomb-api
  (:require
   [clj-http.client :as httpc]))

(defn handle-response
  [{:keys [:status :body]}]
  (if (= 200 status)
    (:results body)
    (throw (ex-info "giantbomb api - upstream service unavailable" {:error body}))))

(defn query-games
  [{:keys [:url :api-key]}
   {:keys [:q :page] :or {page 1}}]
  (handle-response
   (httpc/get url
              {:throw-exceptions false
               :headers          {"User-Agent" "GameRentSampleApp"}
               :as               :json
               :query-params     {:api_key    api-key
                                  :format     "json"
                                  :query      q
                                  :field_list "id,name,image"
                                  :resources  "game"
                                  :page       page}})))
