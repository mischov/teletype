(ns teletype.core
  (:use-macros [dommy.macros :only [sel1]])
  (:require [dommy.core :as dommy]))

;;;;;;;;;;;;
;;  Defs  ;;
;;;;;;;;;;;;

(def to-html
  "Because cljsbuild prepends marked.js to compiled cljs,
   can access via js/-whatever-"
  js/marked)

;;;;;;;;;;;;;;;;;;;;;
;;  Util Functions ;;
;;;;;;;;;;;;;;;;;;;;;

; These are all pretty self-explanatory, but window-height
; and client-height wrap functions around access of those
; respective properties, while set-full-height sets the
; height of an element to a little less than full height.

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
  "Allows two elements of possibly different height to
   scroll together, so that the second element reaches
   the bottom at the same time as the first."
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
  "Reads the value of the textarea with the id 'markdown',
   uses marked to convert that value into html, and sets
   that html as the contents of the div with the id 'preview'."
  []
  (let [markdown (sel1 :#markdown)
        preview (sel1 :#preview)
        md (dommy/value markdown)
        content (to-html md)]
    (dommy/set-html! preview content)))

(defn init
  "Attempts to preview any markdown initially provided,
   sets the markdown and preview elements to full height,
   and creates listeners to preview markdown and sync the
   markdown textarea with the preview div."
  []
  (let [markdown (sel1 :#markdown)
        preview (sel1 :#preview)]
    (preview-md)
    (set-full-height markdown)
    (set-full-height preview)
    (dommy/listen! markdown :keyup preview-md)
    (dommy/listen! markdown :scroll #(sync markdown preview))))

; 'Ey 'O, let's go!
(init)
