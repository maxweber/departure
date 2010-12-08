(ns departure.hello-world-template
  (:use compojure.core
        ring.adapter.jetty
        [ring.util.response :only [response]])
  (:require [compojure.route :as route]
            [net.cgrand.enlive-html :as html]))

(html/deftemplate index "departure/hello_world_template.html"
  [context]
  [:div#message] (html/content (:message context)))

(defn render-to-response "Helper function to render an enlive template" [template]
  (response (apply str template)))

;see also https://github.com/weavejester/compojure/wiki/Getting-Started
(defroutes app
  (GET "/" [] (render-to-response
               (index {:message "Hello World!"})))
  (route/not-found "Page not found"))

(defn start-server []
  ; prepared for interactive development see http://weavejester.github.com/compojure/docs/interactive-development.html
  (future (run-jetty (var app) {:port 8080 :join? false})))

;(def server (start-server))
;(.stop (.get server))
