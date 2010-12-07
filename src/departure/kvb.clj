(ns departure.kvb)

(def *kvb-stations-base-url* "http://www.kvb-koeln.de/german/mofis/")
(def *kvb-station-departure-url* "http://www.kvb-koeln.de/module/response.php?code=")
(def *kvb-page-encoding* "ISO-8859-1")

(defn url-for-initial-letter [initial-letter]
  (str *kvb-stations-base-url* initial-letter "/"))

(defn url-for-code [code]
  (str *kvb-station-departure-url* code))
