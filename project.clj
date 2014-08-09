(defproject teletype "0.1.0-SNAPSHOT"
  :description "Experimentations with Clojurescript Markdown-preview."
  :url "https://github.com/mischov/teletype"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2227"]
                 [prismatic/dommy "0.1.2"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [sablono "0.2.20"]
                 [om "0.7.1"]]
  :plugins [[lein-cljsbuild "1.0.3"]]
  :source-paths ["src-cljs"]
  :resource-paths ["vendor"]
  :cljsbuild {:builds [{:source-paths ["src-cljs"]
                        :compiler {:preamble ["teletype/marked.js"]
                                   :output-to "resources/public/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
