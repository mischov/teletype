(ns teletype.events
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [dommy.macros :refer [sel1]])
  (:require [cljs.core.async :refer [put! chan <!]]
            [dommy.core :as dommy]))

(defn listen
  [element type]
  (let [out (chan)]
    (dommy/listen! element type
                   (fn [e] (put! out (str e))))
    out))

(defn btn-clicks
  []
  (let [clicks (listen (sel1 :#btn) :click)]
    (go (while true
          (dommy/append! (sel1 :#outpt) (<! clicks))))))

(btn-clicks)
