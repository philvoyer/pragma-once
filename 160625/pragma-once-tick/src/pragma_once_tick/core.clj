(ns pragma-once-tick.core
  (:require [quil.core :as quil]
            [quil.middleware :as qm]))

(defn setup []
  (quil/frame-rate 60)
  (quil/no-stroke)
  {:time {:last 0 :elapsed 0}
   :timer {:delay 1000 :current 0}
   :timelapse {:from 0 :now 0}})

(defn tick []
  (quil/fill 255)
  (println "tick" (quil/millis)))

(defn update-time [{:keys [last elapsed]} timestamp]
  {:last timestamp :elapsed (- timestamp last)})

(defn update-timer [{:keys [current delay]} timedelta tick]
  (if (> current delay)
    (do
      (tick)
      {:current (mod current delay) :delay delay})
    {:current (+ current timedelta) :delay delay}))

(defn update-scene [state]
  (assoc state
         :time  (update-time (:time state) (quil/millis))
         :timer (update-timer (:timer state) (:elapsed (:time state)) tick)))

(defn draw [state]
  (quil/rect 0 0 (quil/width) (quil/height))
  (quil/fill 0 7))

(defn click [state event]
  (let [from (:from (:timelapse state))
        now (quil/millis)
        current (:current (:timer state))]
    (assoc state
           :timer {:delay (min (- now from) 3000) :current current}
           :timelapse {:from now})))

(quil/defsketch pragma-once-tick
  :title  "#pragma-once-tick"
  :size  [256 256]
  :setup  setup
  :draw   draw
  :mouse-pressed click
  :update update-scene
  :features   [:resizable]
  :middleware [qm/fun-mode])
