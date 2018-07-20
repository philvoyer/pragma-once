(ns pragma-once-tick.core
  (:require [quil.core :as quil]
            [quil.middleware :as qm]))

(defn tick []
  (quil/fill 255))

(defn setup []
  (quil/frame-rate 60)
  (quil/no-stroke)
  {:time {:last 0 :elapsed 0}
   :timer {:delay 1000 :current 0}
   :timelapse {:from 0 :now 0}
   :click-press false})

(defn update-time [{:keys [last elapsed]} timestamp]
  {:last timestamp :elapsed (- timestamp last)})

(defn update-timer [{:keys [current delay]} timedelta callback]
  (if (> current delay)
    (do
      (callback)
      {:current (mod current delay) :delay delay})
    {:current (+ current timedelta) :delay delay}))

(defn update-scene [state]
  (assoc state
         :time  (update-time (:time state) (quil/millis))
         :timer (update-timer (:timer state) (:elapsed (:time state)) tick))
  state)

(defn draw [state]
  (quil/rect 0 0 (quil/width) (quil/height))
  (quil/fill 0 7))

(defn click-press [state event]
  (assoc state :click-press true))

(defn click-release [state event]
  (let [from (:from (:timelapse state))
        now (quil/millis)
        current (:current (:timer state))]
    (assoc state
           :timer {:delay (min (- now from) 3000) :current current}
           :timelapse {:from now}))
  state)

(quil/defsketch pragma-once-tick
  :title  "#pragma-once-tick"
  :size  [256 256]
  :setup setup
  :draw draw
  :update update-scene
  :mouse-pressed click-press
  :mouse-released click-release
  :features [:resizable]
  :middleware [qm/fun-mode])
