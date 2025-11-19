(ns datastar-patterns.examples.data-computed-example)

(defn present
  []
  [:div.box
   [:h1.header.is-size-1 "data-computed, data-text"]
   [:div.columns
    [:div.column
     [:input.input {:data-bind "last-name"}]
     [:div.select
      [:select {:data-bind "title"}
       [:option {:value "Mr"} "Mr"]
       [:option {:value "Mrs"} "Mrs"]
       [:option {:value "Sr"} "Sr"]
       [:option {:value "Sra"} "Sra"]]]]
    [:div.column
     [:p {:disabled true :data-computed:fullname "$title + ' ' + $last-name" :data-text "$fullname"}]]]
   [:p "An intermediate variable $fullname is created from $title and $last-name, then shown using data-text"]])
