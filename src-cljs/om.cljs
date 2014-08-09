(ns teletype.om
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async]
            [goog.events :as events]
            [teletype.store :as store]
            [teletype.store.localstore :as localstore])
  (:import
   [goog.events EventType]
   [goog.async Throttle]))


;;;;;;;;;;;;;;;;
;;  Markdown  ;;
;;;;;;;;;;;;;;;;


(def to-html
  "Because cljsbuild prepends marked.js to compiled cljs,
   can access via js/{{whatever}}."
  js/marked)


;;;;;;;;;;;;;;;;;;;;;;;
;;  Height and Sync  ;;
;;;;;;;;;;;;;;;;;;;;;;;


(defn window-height
  []
  
  js/window.innerHeight)


(defn client-height
  []
  
  js/document.documentElement.clientHeight)


(defn full-height
  []

  (or (window-height) (client-height)))


(defn sync-scroll-top
  [markdown preview]
  (let [scrollheight1 (.-scrollHeight markdown)
        scrollheight2 (.-scrollHeight preview)
        ratio (/ (- (client-height) scrollheight1)
                 (- (client-height) scrollheight2))
        scrolltop (/ (.-scrollTop markdown) ratio)]
    (set! (.-scrollTop preview) scrolltop)))


(defn markdown-scroll
  [markdown preview]

  (let [sync (Throttle. #(sync-scroll-top markdown preview) 50)]
    (events/listen markdown goog.events.EventType/SCROLL
                   #(.fire sync))))


;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Save and Autosave  ;;
;;;;;;;;;;;;;;;;;;;;;;;;;


(defn set-timeout
  [timeout f]

  (js/setTimeout f timeout))


(defn clear-timeout
  [d]

  (js/clearTimeout d))


;;;;;;;;;;
;;  Om  ;;
;;;;;;;;;;


(defn autosave
  [owner time f]

  (let [old-timeout (om/get-state owner :autosave)
        new-timeout (set-timeout time f)]
    (when old-timeout
      (clear-timeout old-timeout))
    (om/set-state! owner :timeout new-timeout)))


(defn save-markdown
  [owner markdown]

  (let [localstore (om/get-state owner :localstore)]
    (store/write-md localstore markdown)))


(defn handle-change
  [e owner {:keys [markdown]}]

  (om/set-state! owner :markdown (.. e -target -value))
  (autosave owner 1000 #(save-markdown owner markdown)))


(defn teletype-view
  [app owner]

  (reify
    om/IInitState
    (init-state [_]
      {:markdown ""
       :autosave nil
       :localstore (localstore/create-store "test")})

    om/IDidMount
    (did-mount [_]
      (markdown-scroll (om/get-node owner "markdown")
                       (om/get-node owner "preview"))

      (let [localstore (om/get-state owner :localstore)
            md (store/read-md localstore)]
        (when (seq md)
          (om/set-state! owner :markdown md))))

    om/IRenderState
    (render-state [_ {:keys [markdown] :as state}]
      (html
       [:div {:id "container"}
        [:div {:id "left-column"}
         [:textarea {:id "markdown"
                     :ref "markdown"
                     :autofocus true
                     :value markdown
                     :style {:height (full-height)}
                     :on-change #(handle-change % owner state)}]]
       
        [:div {:id "right-column"}
         [:div {:id "preview"
                :ref "preview"
                :style {:height (full-height)}
                :dangerouslySetInnerHTML {:__html (to-html markdown)}}]]]))))


(defn render-teletype
  [app-state]

  (om/root
   teletype-view app-state
   {:target (. js/document (getElementById "app"))}))


(defn init
  []

  (render-teletype {}))
