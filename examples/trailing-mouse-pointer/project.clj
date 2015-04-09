(defproject matthiasn/trailing-mouse-pointer "0.1.0-SNAPSHOT"
  :description "Sample application built with systems-toolbox library"
  :url "https://github.com/matthiasn/systems-toolbox"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [ch.qos.logback/logback-classic "1.1.2"]
                 [com.taoensso/sente "1.4.1"]
                 [com.cognitect/transit-clj  "0.8.259"]
                 [com.cognitect/transit-cljs "0.8.205"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [http-kit "2.1.19"]
                 [compojure "1.3.3"]
                 [hiccup "1.0.5"]
                 [hiccup-bridge "1.0.1"]
                 [garden "1.2.5"]
                 [clj-pid "0.1.1"]
                 [matthiasn/systems-toolbox "0.1.24-SNAPSHOT"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.4"]
                 [clj-time "0.9.0"]
                 [org.clojure/clojurescript "0.0-3126"]
                 [reagent "0.5.0"]]

  :source-paths ["src/clj/"]

  :clean-targets ^{:protect false} ["resources/public/js/build/"]

  :main example.core

  :plugins [[lein-cljsbuild "1.0.5"]
            [codox "0.8.10"]]

  :cljsbuild {:builds [{:id "release"
                        :source-paths ["src/cljs"]
                        :compiler {:output-dir "resources/public/js/build/"
                                   :output-to "resources/public/js/build/example.js"
                                   :optimizations :simple
                                   :source-map "resources/public/js/build/example.js.map"}}]})