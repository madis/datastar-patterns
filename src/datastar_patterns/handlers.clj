(ns datastar-patterns.handlers
  (:require
   [datastar-patterns.examples.data-bind-example :as data-bind-example]
   [datastar-patterns.examples.data-class-example :as data-class-example]
   [datastar-patterns.examples.data-computed-example :as data-computed-example]
   [datastar-patterns.examples.data-on-example :as data-on-example]
   [datastar-patterns.examples.server-progress-example :as server-progress-example]
   [datastar-patterns.helpers :refer [get-signals with-sse]]
   [hiccup2.core :as h]
   [starfederation.datastar.clojure.api :as d*]))


(defn landing-html
  [body-content]
  [:html
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:title "Datastar Playground"]
    [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/npm/bulma@1.0.4/css/bulma.min.css"}]
    [:link {:rel "stylesheet" :href "/assets/css/style.css"}]
    [:script {:type :module
              :src "https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.0-RC.6/bundles/datastar.js"}]
    [:script {:src "https://kit.fontawesome.com/cb70718952.js" :crossorigin "anonymous"}]]
   [:body body-content]])

(defn render-html
  [hiccup]
  (-> hiccup (h/html ,,,) (str ,,,)))

(defn layout
  [content]
  [:div.section
   [:div.container content]])

(defn landing-page
  [req]
  (let [page (list
               (data-bind-example/present)
               (data-class-example/present)
               (data-computed-example/present)
               (data-on-example/present)
               (server-progress-example/present))]
    {:status 200 :body (-> page layout landing-html render-html)}))

(defn server-actions
  [req]
  (if (d*/datastar-request? req)
    (with-sse req
      (fn [send!]
        (let [speed (:speed (get-signals req))
              time-to-sleep (* (max 1 speed) 10)]
          (doseq [progress (range 101)]
            (send! :signals {:server-progress (str progress)})
            (Thread/sleep time-to-sleep)))))
    {:status 200 :body "This endpoint is for datastar SSE connections"}))
