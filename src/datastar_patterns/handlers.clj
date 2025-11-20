(ns datastar-patterns.handlers
  (:require
   [datastar-patterns.examples.data-bind-example :as data-bind-example]
   [datastar-patterns.examples.data-class-example :as data-class-example]
   [datastar-patterns.examples.data-computed-example :as data-computed-example]
   [datastar-patterns.examples.data-on-example :as data-on-example]
   [datastar-patterns.examples.multiple-sse-receivers-example :as multiple-sse-receivers-example]
   [datastar-patterns.examples.server-patch-mode-example :as server-patch-mode-example]
   [datastar-patterns.examples.server-progress-example :as server-progress-example]
   [datastar-patterns.helpers :as helpers :refer [get-signals with-sse]]
   [hiccup2.core :as h]
   [starfederation.datastar.clojure.adapter.common :refer [on-open]]
   [starfederation.datastar.clojure.adapter.http-kit :refer [->sse-response]]
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
               (server-progress-example/present)
               (server-patch-mode-example/present)
               (multiple-sse-receivers-example/present))]
    {:status 200 :body (-> page layout landing-html render-html)}))

(defn- start-progress-example
  [req send!]
  (let [speed (:speed (get-signals req))
              time-to-sleep (* (max 1 speed) 10)]
          (doseq [progress (range 101)]
            (send! :signals {:server-progress (str progress)})
            (Thread/sleep time-to-sleep))))

(defn- patch-mode-example
  [req send!]
  (let [signals (get-signals req)
        message (get signals :message)
        patch-mode (get signals :patch-mode)]
    (send! :html
           [:div.notification.is-info "Your message was received: " [:strong message]]
           {:patch-mode patch-mode :selector "#target-container"})))

(def sse-connections (atom {}))

(defn- send-to-multiple-example
  [req send!]
  (let [message-text (get (get-signals req) :multi-message)
        message (fn [msg] [:div.notification.is-info "New message: " [:strong msg]])]
    (doseq [[client-id sse-connection] @sse-connections]
      (send! :html
             (message (str "sent to " client-id))
             {:patch-mode "append" :selector "#sender-messages-container"})
      (helpers/send! sse-connection
                     :html
                     (message message-text)
                     {:patch-mode "append" :selector "#receiver-messages-container"}))))

(defn server-actions
  [req]
  (if (d*/datastar-request? req)
    (with-sse req
      (fn [send!]
        (let [action (get-in req [:params :action])]
          (case action
            "start-progress" (start-progress-example req send!)
            "patch-mode" (patch-mode-example req send!)
            "multiple-sse-receivers" (send-to-multiple-example req send!)))))
    {:status 200 :body "This endpoint is for datastar SSE connections"}))

(defn sse-receiver
  [req]
  (if (d*/datastar-request? req)
    (->sse-response req
                    {on-open (fn [sse]
                               (swap! sse-connections assoc (get-in req [:params :client-id]) sse))})
    {:status 200
     :body (-> {:client-id (get-in req [:params :client-id])}
               multiple-sse-receivers-example/receiver
               layout
               landing-html
               render-html)}))
