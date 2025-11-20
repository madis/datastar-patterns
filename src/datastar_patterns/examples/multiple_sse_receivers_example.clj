(ns datastar-patterns.examples.multiple-sse-receivers-example)

(defn present
  []
  [:div.box
   [:h1.header.is-size-1 "data-init & multiple SSE receivers"]
   [:div.columns
    [:div.column
     [:div
      [:p [:a {:href "/sse-receiver?client-id=first" :target "_blank"} "First"]]
      [:p [:a {:href "/sse-receiver?client-id=second" :target "_blank"} "Second"]]
      [:p [:a {:href "/sse-receiver?client-id=third" :target "_blank"} "Third"]]]
     [:div.field
      [:label.label "Message"
       [:input.input {:type "text" :data-bind "multi-message"}]]]
     [:button.button {:data-on:click "@post('/server-actions?action=multiple-sse-receivers')"} "Send!"]]
    [:div.column
     [:div {:id "sender-messages-container"}]]]
   [:p "By clicking the links, new browser tabs open that will retain the SSE connection open. Clicking [Send] will send and show message in each of them"]])

(defn receiver
  [{:keys [client-id]}]
  [:div.box {:data-init (str "@post('/sse-receiver?client-id=" client-id "')")}
   [:h1.header.is-size-1 "Incoming messages for:" [:strong client-id]]
   [:div {:id "receiver-messages-container"}]])
