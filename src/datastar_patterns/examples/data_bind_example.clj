(ns datastar-patterns.examples.data-bind-example)


(defn present
  []
  [:div.box
   [:h1.header.is-size-1 "data-bind"]
   [:div.columns
    [:div.column
     [:input.input {:data-bind "$input-text"}]]
    [:div.column
     [:input.input {:disabled true :data-bind "$input-text"}]]]
   [:p "The first text element creates binding to $input-text and when the text input value changes, so does $input-text, resulting updates in the second text input (readonly)"]])
