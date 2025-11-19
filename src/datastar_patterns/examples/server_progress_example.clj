(ns datastar-patterns.examples.server-progress-example)

(defn present
  []
  [:div.box
   [:h1.header.is-size-1 "data-bind"]
   [:div.columns
    [:div.column
     [:input.input {:data-bind "$speed"}]
     [:button.button {:data-on:click "@post('/server-actions')"} "Start!"]]
    [:div.column
     [:progress.progress {:value 0 :max 100 :data-attr:value "$server-progress"}]]]
   [:p "Clicking the button invokes server action which through SSE sends a sequence of signals that update the progress bar element"]])
