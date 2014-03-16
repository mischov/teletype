(defproject teletype "0.1.0-SNAPSHOT"
  :description "Experimentations with Clojurescript Markdown-preview."
  :url "https://github.com/mischov/teletype"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2156"]
                 [prismatic/dommy "0.1.2"]]
  :plugins [[lein-cljsbuild "1.0.2"]]
  :source-paths ["src-cljs"]
  :resource-paths ["vendor"]
  :cljsbuild {:builds [{:source-paths ["src-cljs"]
                        :compiler {:preamble ["teletype/marked.js"]
                                   :output-to "resources/public/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
