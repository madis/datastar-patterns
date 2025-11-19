(ns datastar-patterns.examples.data-class-example)

(defn present
  []
  [:div.box
   [:h1.header.is-size-1 "data-attr"]
   [:div.columns
    [:div.column
     [:div.field
      [:label.label "Danger"
       [:input {:type "radio" :checked true :value "button is-danger" :data-bind "selected-class"}]]]
     [:div.field
      [:label.label "Link"
       [:input {:type "radio" :value "button is-link" :data-bind "selected-class"}]]]
     [:div.field
      [:label.label "Warning"
       [:input {:type "radio" :value "button is-warning" :data-bind "selected-class"}]]]
     [:div.field
      [:label.label "Success"
       [:input {:type "radio" :value "button is-success" :data-bind "selected-class"}]]]]
    [:div.column
     [:button.button.is-primary {:data-attr:class "$selected-class"} "I am a button"]]]
   [:p "Selecting a radio option changes the value of $selected-class which gets reflected on the button via data-attr"]])
