(ns departure.kvb-station-details)

(def *kvb-detail-url* "http://www.kvb-koeln.de/module/response.php?code=")

(defn url-for-code [c]
  (str *kvb-base-url* c "/"))
