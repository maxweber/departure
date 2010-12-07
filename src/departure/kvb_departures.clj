(ns departure.kvb-departures
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as stri])
  (:use [clojure.test]
        [clojure.string :only [trim]]
        [departure.kvb]
        [departure.utils]))

(defn extract-table-rows [html]
  (html/select html #{[:td]}))

(with-test
 (defn heading? [element]
   (and (map? element) (= :b (:tag element))))
 (is (true? (heading? {:tag :b, :attrs nil,
                      :content
                      '("Universitätsstraße, ? Frechen/Weiden West ")})))
 (is (false? (heading? "Weiden West (4 Minuten)"))))


(defn extract-string [s]
  (if (nil? s) nil (trim (stri/replace s "," ""))))

(with-test
  (defn extract-heading [heading-text]
    (let [parts (stri/split heading-text #"→")]
      (into [] (map extract-string parts))))
  (is (= ["Universitätsstraße" "Frechen/Weiden West"]
           (extract-heading "Universitätsstraße, → Frechen/Weiden West ")))
  (is (= ["Alter Miltäring" "Neumarkt"]
           (extract-heading "Alter Miltäring → Neumarkt ")))
  (is (= ["Rodenkirchen Bahnhof"]
           (extract-heading "Rodenkirchen Bahnhof "))))


(def re-departure-detail #"(.*) \((.*)\).*")

(with-test
  (defn get-content [element]
    (first (:content element)))
  (is (= "the content" (get-content {:tag :b, :attrs nil,
                      :content
                      '("the content")}))))

(with-test
  (defn extract-departure-detail [element]
    (let [re-time #"([0-9]+).*"
          [direction time] (drop 1 (re-find re-departure-detail element))
          time (if (re-matches re-time time)
                 (read-string (second (re-find re-time time)))
                 0)]
     [direction time]))
  (is (= (extract-departure-detail "Junkersdorf (11 Minuten)")
         ["Junkersdorf" 11]))
  (is (= (extract-departure-detail "Klettenberg (Sofort)")
         ["Klettenberg" 0])))

(with-test
  (defn departure-detail? [element]
    (and (string? element) (not
                     (nil? (re-matches re-departure-detail element)))))
  (is (= true (departure-detail? "Klettenberg (Sofort)")))
  (is (= true (departure-detail? "Junkersdorf (11 Minuten)")))
  (is (= false (departure-detail? "Junkersdorf"))))

(with-test
  (defn group-separator? [element]
    (= " " element))
  (is (= true (group-separator? " ")))
  (is (= false (group-separator? "something"))))


(defn extract-depature-times [html]
  (let [table-rows (extract-table-rows html)
        table-rows (map #(first (:content %)) table-rows)
        relevant-rows (filter
                       #(or (departure-detail? %) (heading? %)
                            (group-separator? %))
                       table-rows)
        relevant-rows (map
                       #(cond (heading? %) (extract-heading (get-content %))
                              (departure-detail? %) (extract-departure-detail %)
                              :else %)
                       relevant-rows)]
    (remove #(group-separator? (first %))  (partition-by group-separator? relevant-rows))
    ))

; from here only impure functions
(defn fetch-and-extract-depature-times [code]
  (let [url (url-for-code code)
        html (fetch-url url *kvb-page-encoding*)
        data (extract-depature-times html)]
    data))

; a demo

(defn demo []
  (fetch-and-extract-depature-times "WEH"))
