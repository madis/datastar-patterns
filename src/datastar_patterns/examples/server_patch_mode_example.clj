(ns datastar-patterns.examples.server-patch-mode-example)

(defn present
  []
  [:div.box
   [:h1.header.is-size-1 "patch-mode"]
   [:div.columns
    [:div.column
     [:div.field
      [:label.label "Message"
       [:input.input {:type "text" :data-bind "message"}]]]
        [:div.select
         [:select {:data-bind "patch-mode"}
          [:option {:value "inner" :selected true} "inner"]
          [:option {:value "append"} "append"]]]
     [:button.button {:data-on:click "@post('/server-actions?action=patch-mode')"} "Send!"]]
    [:div.column
     [:div {:id "target-container"}]]]
   [:p "Depending on the patch-mode parameter, the HTML sent from the back-end within SSE connection, the DOM will b modified. Here only inner and append patch-modes are demonstrated. The default is 'outer'"]
   [:a {:href "https://data-star.dev/reference/sse_events#datastar-patch-elements"
        :target "_blank"} "datastar-patch-elements documentation"]])

