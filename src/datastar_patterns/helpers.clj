(ns datastar-patterns.helpers
  (:require
    [clojure.walk :as walk]
    [charred.api :as charred]
    [hiccup2.core :as h]
    [starfederation.datastar.clojure.adapter.http-kit :refer [->sse-response on-open]]
    [starfederation.datastar.clojure.api :as d*]
    [starfederation.datastar.clojure.api.common :as d*-common]
    ))

(def ^:private bufSize 1024)
(def read-json (charred/parse-json-fn {:async? false :bufsize bufSize}))
(def ->json charred/write-json-str)

(defn opts->d*-opts
  [opts]
  (let [conversion-map {:selector d*-common/selector
                        :patch-mode d*-common/patch-mode}]
    (reduce (fn [acc [k v]]
                 (if (contains? conversion-map k)
                   (assoc acc (get conversion-map k) v)
                   (throw (ex-info "Unknown datastar option provided:"
                                   {:unknown-key k :known-keys (keys conversion-map)}))))
            {}
            opts)))

(defn get-signals [req]
  (some-> req d*/get-signals read-json walk/keywordize-keys))

(defn send-html
  "d*-opts passed to starfederation.datastar.clojure.api/patch-elements!"
  [sse hiccup d*-opts]
  (d*/patch-elements! sse (-> hiccup (h/html ,,,) (str ,,,)) (if d*-opts (opts->d*-opts d*-opts) {}))
  sse)

(defn send-signals [sse signals]
  (d*/patch-signals! sse (->json signals))
  sse)

(defn send-cljs [sse forms]
  (let [script-id (str (random-uuid))
        run-scittle (str "scittle.core.eval_script_tags([document.getElementById('" script-id "')])")]
    (d*/execute-script! sse forms {d*-common/attributes {:type "application/x-scittle" :id script-id}
                                   d*-common/auto-remove false})
    (d*/execute-script! sse run-scittle)
    sse))

(defn send! [sse type data & [extra]]
  (case type
    :html (do
            (send-html sse data extra))
    :to-url (d*/execute-script! sse (str "window.location.href = '" data "';"))
    :signals (send-signals sse data)
    :js (d*/execute-script! sse data)
    :cljs (send-cljs sse data)
    ))

(defn with-sse
  "Passes the callback send-html function after SSE connection has been opened.
  send-html takes a vector of hiccup forms and uses datastar to patch respective
  elements in the browser (identified by element id).

  Allows sending HTML to front-end multiple times, not just one return value.

  Another benefit of using this is that the send-html can be passed to other layers,
  which avoids the tight coupling and source-code dependency on framework

  Example:

  (defn my-route-handler [req]
    (with-sse
      req
      (fn [send!]
        (send! :html [:div#my-id \"Starting some work\"])
        (send! :signals {:info-class \"is-hidden\"})
        ;; Do some more things
        (send! :html [:div#my-id \"Finished processing\"]))))"
  [req callback]
  (->sse-response
    req
    {on-open
     (fn [sse]
       (callback (partial send! sse))
       (Thread/sleep 50))}))
