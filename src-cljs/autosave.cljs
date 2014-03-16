(ns teletype.autosave)

(defn set-timeout
  [timeout f]
  (js/setTimeout f timeout))

(defn clear-timeout
  [d]
  (js/clearTimeout d))

(defn save
  [last-timeout timeout f]
  (let [timeout-id (set-timeout timeout f)]
    (when @last-timeout
      (clear-timeout @last-timeout))
    (reset! last-timeout timeout-id)))
