(ns teletype.dommy
  (:use-macros [dommy.macros :only [sel1]])
  (:require [dommy.core :as dommy]
            [teletype.store :as store]
            [teletype.store.localstore :as localstore]
            [teletype.autosave :as autosave]))

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

(defn update-preview
  [markdown]
  (let [preview (sel1 :#preview)
        html (to-html markdown)]
    (dommy/set-html! preview html)))

(defn save-markdown
  [state store markdown]
  (autosave/save state 1000 #(store/write-md store markdown)))

(defn process-markdown
  [state store]
  (let [markdown-elem (sel1 :#markdown)
        markdown (dommy/value markdown-elem)]
    (update-preview markdown)
    (save-markdown state store markdown)))

(defn init
  "Attempts loads then previews any markdown stored in
   localStorage, sets the markdown and preview elements
   to full height, and creates listeners to save and
   preview markdown and sync the markdown textarea with
   the preview div."
  []
  (let [markdown (sel1 :#markdown)
        preview (sel1 :#preview)
        state (atom nil)
        store (localstore/create-store "test")
        saved-md (store/read-md store)]
    (when saved-md
      (dommy/set-value! markdown saved-md))
    (process-markdown state store)
    (set-full-height markdown)
    (set-full-height preview)
    (dommy/listen! markdown
                   :keyup #(process-markdown state store)
                   :scroll #(sync markdown preview))))

; 'Ey 'o, let's go!
; (init)

