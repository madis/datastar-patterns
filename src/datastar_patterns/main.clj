(ns datastar-patterns.main
  (:require
    [reitit.core]
    [reitit.ring :as rr]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.session :refer [wrap-session]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.multipart-params :refer [wrap-multipart-params]]
    [org.httpkit.server :as hk-server]
    [datastar-patterns.handlers :as handlers])
  (:gen-class))


(defn router
 [deps]
 (rr/router
   [["/" {:get handlers/landing-page}]
    ["/server-actions" {:post handlers/server-actions}]
    ["/sse-receiver" {:get handlers/sse-receiver
                      :post handlers/sse-receiver}]
    ["/assets/*" (-> (rr/create-resource-handler)
                     (wrap-cors ,,,
                                :access-control-allow-origin #".*"
                                :access-control-allow-methods [:get])
                     (wrap-content-type ,,, {:mime-types {"cljs" "application/x-scittle"}}))]]))

(defn handler [router]
  (-> (rr/ring-handler router nil)
      wrap-keyword-params
      wrap-params
      wrap-multipart-params
      wrap-session))

(defn start!
  ([] (start! (handler (router {}))))

  ([handler & {:as opts}]
   (let [opts (merge {:port 3000 :join? false :legacy-return-value? false}
                    opts)]
    (println "Starting server with opts:" opts "with handler:" handler)
    (hk-server/run-server handler opts))))

(defn stop! [server]
  (println "Stopping server" server)
  (deref (hk-server/server-stop! server))
  (hk-server/server-join server))

(defonce server-instance (atom nil))

(defn before-ns-unload []
  (when @server-instance (stop! @server-instance))
  (reset! server-instance nil))

(defn after-ns-reload []
  (reset! server-instance (start!)))

(defn -main [& args]
  (println "Starting datastar-playground.main")
  (reset! server-instance (start!)))

