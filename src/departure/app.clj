(ns departure.app
  (:use compojure.core
        ring.adapter.jetty
        [ring.util.response :only [response]]
        [net.cgrand.enlive-html
         :only [deftemplate defsnippet content clone-for
                nth-of-type first-child do-> set-attr sniptest at emit*]])
  (:require [compojure.route :as route]
            [net.cgrand.enlive-html :as html]
            [departure.kvb-departures :as kvb]))

(def template-path "departure/departure.html")


(def *departure-sel* [[:.content (nth-of-type 1)] :> first-child])
(defsnippet departure-model template-path *departure-sel*
  [{:keys [departure-in endoftheline]}]
  [:.endoftheline] (content endoftheline)
  [:.minutes] (content (str departure-in))
  )

; we only want to select the model h2 ul range
(def *direction-sel* {[:.direction] [[:.content (nth-of-type 1)]]})
(defsnippet direction-model template-path *direction-sel*
  [{:keys [direction departures]} model]
  [:.direction] (content direction)
  [:.content] (content (map #(model %) departures))
  )

(html/deftemplate index template-path
  [{:keys [station directions]}]
  [:#title] (html/content (str "Haltestelle " station))
  [:.station] (html/content (str "Haltestelle " station))
  [:.directions] (content (map #(direction-model % departure-model) directions)))

(defn render-to-response "Helper function to render an enlive template" [template]
  (response (apply str template)))

;see also https://github.com/weavejester/compojure/wiki/Getting-Started
(defroutes app
  (GET "/departures/:code" [code] (render-to-response
               (index (kvb/departures code))))
  (route/not-found "Page not found"))

(defn start-server []
  ; prepared for interactive development see http://weavejester.github.com/compojure/docs/interactive-development.html
  (future (run-jetty (var app) {:port 8080 :join? false})))

;(def server (start-server))
;(.stop (.get server))

;see https://github.com/swannodette/enlive-tutorial/ for a full
;tutorial of enlive
