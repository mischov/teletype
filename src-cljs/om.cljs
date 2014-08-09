(ns teletype.om
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async]
            [goog.events :as events])
  (:import
   [goog.events EventType]
   [goog.async Throttle]))


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

;;;;;;;;;;
;;  Om  ;;
;;;;;;;;;;


(defn handle-change
  [e owner {:keys [markdown]}]

  (om/set-state! owner :markdown (.. e -target -value)))


(defn teletype-view
  [app owner]

  (reify
    om/IInitState
    (init-state [_]
      {:markdown ""})

    om/IDidMount
    (did-mount [_]
      (markdown-scroll (om/get-node owner "markdown")
                       (om/get-node owner "preview")))

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
