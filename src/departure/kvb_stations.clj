(ns departure.kvb-stations
  "Extracts all stations from the kvb-koeln.de homepage (http://www.kvb-koeln.de/german/mofis/mofis.html)"
  (:require [net.cgrand.enlive-html :as html])
  (:use [clojure.test]
        departure.kvb
        departure.utils))

(comment
  "An example element with an onclick link to the detail page of the station Weißhausstraße with
the KVB code \"WEH\"
...
<td>
<input type=\"button\" value=\"Weißhausstr.\" class=\"hst\"
onclick=\"self.location.href='/german/mofis/W/WEH'\">
</td>
...")

(with-test
  (defn extract-code
    "Extracts the KVB code out of the onclick part of an input button link to a station detail page."
    [href]
   (second (re-find #"'.*/(.*)'" href)))
  (is (= "WEH" (extract-code "self.location.href='/german/mofis/W/WEH'"))))

(defn extract-stops-elements "Extracts all input elements with the \"hst\" class from the html." [html]
  (html/select html #{[:input.hst]}))

(defn extract-stops-data "Extracts the station's name and code of an input \"hst\" element." [elem]
  (let [{:keys [attrs]} elem
        {v :value h :onclick} attrs
        h (extract-code h)]
    [(keyword h) v])) ; this function and its context would be easier to understand with a test ;-)
                      ; in this case treat it as negative example of not using tests.

(defn extract-stops "Extracts all stations codes and names from the html
 of a stations listing page like \"http://www.kvb-koeln.de/german/mofis/W/\"."[html]
  (into {} (map extract-stops-data (extract-stops-elements html))))


; from here only impure functions
(defn fetch-and-extract-stops "Fetch the data from a URL and extract all station details for the given initial letter." [initial-letter]
  (let [url (url-for-initial-letter initial-letter)
        html (fetch-url url *kvb-page-encoding*)
        data (extract-stops html)]
    data))

; a demo
(defn demo []
  (into {}
        (apply concat (map fetch-and-extract-stops (char-range \A \Z)))))
