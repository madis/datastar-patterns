(ns datastar-patterns.examples.data-on-example)

(defn present
  []
  [:div.box
   [:h1.header.is-size-1 "data-on"]
   [:div.columns
    [:div.column
     [:figure.image.is-128x128 {:data-on:pointermove "$x-coord = evt.layerX; $y-coord = evt.layerY"}
      [:img {:src "https://bulma.io/assets/images/placeholders/128x128.png"}]]]
    [:div.column
     [:input.input {:disabled true :data-attr:value "$x-coord"}]
     [:input.input {:disabled true :data-attr:value "$y-coord"}]]]
   [:p "data-on:pointermove sets $x-coord and $y-coord and the value gets reflected in the text boxes"]])
