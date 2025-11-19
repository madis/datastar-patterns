(ns user
  (:require
    [clj-reload.core :as reload]
    [datastar-patterns.main]))

(alter-var-root #'*warn-on-reflection* (constantly true))

(reload/init
  {:no-reload '#{user}})

(defn go! []
  (reload/reload))

(comment
  (datastar-patterns.main/after-ns-reload)
  (go!))

