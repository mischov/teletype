(ns teletype.core
  (:use-macros [dommy.macros :only [sel1]])
  (:require [dommy.core :as dommy]))

;;;;;;;;;;;;
;;  Defs  ;;
;;;;;;;;;;;;

(def to-html js/marked)

;;;;;;;;;;;;;;;;;;;;;
;;  Util Functions ;;
;;;;;;;;;;;;;;;;;;;;;

(defn window-height
  []
  js/window.innerHeight)

(defn client-height
  []
  js/document.documentElement.clientHeight)

(defn set-full-height
  [elem]
  (let [h (or (window-height) (client-height))]
    (dommy/set-style! elem :height (str (- h 25) "px"))))

(defn sync
  [e1 e2]
  (let [e1-scrollheight (.-scrollHeight e1)
        e2-scrollheight (.-scrollHeight e2)
        ratio (/ (- (client-height) e1-scrollheight)
                 (- (client-height) e2-scrollheight))
        scrolltop (/ (.-scrollTop e1) ratio)]
    (set! (.-scrollTop e2) scrolltop)))

;;;;;;;;;;;;
;;  Main  ;;
;;;;;;;;;;;;

(defn preview-md
  []
  (let [markdown (sel1 :#markdown)
        preview (sel1 :#preview)
        md (dommy/value markdown)
        content (to-html md)]
    (dommy/set-html! preview content)))

(defn init
  []
  (let [markdown (sel1 :#markdown)
        preview (sel1 :#preview)]
    (preview-md)
    (set-full-height markdown)
    (set-full-height preview)
    (dommy/listen! markdown :keyup preview-md)
    (dommy/listen! markdown :scroll #(sync markdown preview))))

(init)
