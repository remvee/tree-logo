(defproject tree-logo "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.9.1"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.773"]
                 [org.clojure/core.async  "1.3.610"]
                 [reagent "0.10.0"]]

  :plugins [[lein-figwheel "0.5.19"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src"]
                :figwheel     {}
                :compiler     {:main                 tree-logo.core
                               :asset-path           "js/out"
                               :output-to            "resources/public/js/tree_logo.js"
                               :output-dir           "resources/public/js/out"
                               :source-map-timestamp true}}
               {:id           "min"
                :source-paths ["src"]
                :compiler     {:output-to     "resources/public/js/tree_logo.js"
                               :main          tree-logo.core
                               :optimizations :advanced
                               :pretty-print  false}}]}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev {:dependencies  [[figwheel-sidecar "0.5.20"]]
                   :source-paths  ["src" "dev"]
                   :clean-targets ^{:protect false} ["resources/public/js" :target-path]}})
