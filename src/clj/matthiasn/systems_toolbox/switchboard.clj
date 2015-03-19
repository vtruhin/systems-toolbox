(ns matthiasn.systems-toolbox.switchboard
  (:gen-class)
  (:require [clojure.core.match :refer [match]]
            [clojure.core.async :refer [put! sub tap]]
            [matthiasn.systems-toolbox.component :as comp]
            [matthiasn.systems-toolbox.log :as l]
            [matthiasn.systems-toolbox.sente :as ws]))

(defn make-comp
  [app put-fn cfg]
  (let [{:keys [cmp-id mk-state-fn handler-fn state-pub-handler-fn opts]} cfg
        cmp (comp/make-component mk-state-fn handler-fn state-pub-handler-fn opts)]
    (put-fn [:log/switchboard-init cmp-id])
    (swap! app assoc-in [:components cmp-id] cmp)))

(defn subscribe-component
  "Subscribe component to a specified publisher."
  [app put-fn params]
  (let [pub-comp ((:pub-comp params) (:components @app))
        sub-comp ((:sub-comp params) (:components @app))]
    (sub (:state-pub pub-comp) :app-state (:sliding-in-chan sub-comp))
    (put-fn [:log/switchboard-sub (str (:pub-comp params) "->" (:sub-comp params))])
    (swap! app update-in [:subs] conj params)))

(defn tap-comp
  "Tap component in channel to a specified mult."
  [app put-fn params]
  (let [mult-comp ((:mult-comp params) (:components @app))
        tap-comp  ((:tap-comp params)  (:components @app))]
    (tap (:out-mult mult-comp) (:in-chan tap-comp))
    (put-fn [:log/switchboard-tap (str (:mult-comp params) "->" (:tap-comp params))])
    (swap! app update-in [:taps] conj params)))

(defn make-ws-comp
  "Initializes Sente / WS component and makes is accessible under [:components :ws]
  inside the switchboard state atom."
  [app put-fn]
  (let [ws (ws/component)]
    (swap! app assoc-in [:components :ws] ws)
    (put-fn [:log/switchboard-init :ws])
    ws))

(defn make-log-comp
  "Creates a log component."
  [app put-fn]
  (let [log-comp (l/component)]
    (swap! app assoc-in [:components :log] log-comp)
    (put-fn [:log/switchboard-init :log])
    log-comp))

(defn self-register
  ""
  [app put-fn self]
  (swap! app assoc-in [:components :switchboard] self))

(defn make-state
  "Return clean initial component state atom."
  [put-fn]
  (let [app (atom {:components {}
                   :subs #{}
                   :taps #{}})]
    app))

(defn in-handler
  "Handle incoming messages: process / add to application state."
  [app put-fn msg]
  (match msg
         [:cmd/self-register self] (self-register app put-fn self)
         [:cmd/make-comp      cmp] (make-comp app put-fn cmp)
         [:cmd/make-ws-comp      ] (make-ws-comp app put-fn)
         [:cmd/make-log-comp     ] (make-log-comp app put-fn)
         [:cmd/sub-comp    params] (subscribe-component app put-fn params)
         [:cmd/tap-comp    params] (tap-comp app put-fn params)
         :else (prn "unknown msg in switchboard-in-loop" msg)))

(defn component
  "Creates a switchboard component that wires individual components together into
  a communicating system."
  []
  (prn "Switchboard starting.")
  (let [switchboard (comp/make-component make-state in-handler nil)
        sw-in-chan (:in-chan switchboard)]
    (put! sw-in-chan [:cmd/self-register switchboard])
    (put! sw-in-chan [:cmd/make-log-comp])
    (put! sw-in-chan [:cmd/tap-comp {:mult-comp :switchboard :tap-comp :log}])
    switchboard))