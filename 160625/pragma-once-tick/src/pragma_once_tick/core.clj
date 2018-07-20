(ns pragma-once-tick.core
  (:require [quil.core :as quil]
            [quil.middleware :as qm]))

(def timer-delay-initial 1000)
(def timer-delay-max 3000)

(defn tick []
  (quil/fill 255))

(defn setup []
  (quil/frame-rate 60)
  (quil/no-stroke)
  {:time {:last 0 :elapsed 0}
   :timer {:delay timer-delay-initial :current 0}
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
         :timer (update-timer (:timer state) (:elapsed (:time state)) tick)))

(defn draw [state]
  (quil/rect 0 0 (quil/width) (quil/height))
  (quil/fill 0 7))

(defn click-press [state event]
  (let [from (:from (:timelapse state))
        now (quil/millis)
        current (:current (:timer state))]
    (assoc state
           :timer {:delay (min (- now from) timer-delay-max) :current current}
           :timelapse {:from now})))

(quil/defsketch pragma-once-tick
  :title  "#pragma-once-tick"
  :size  [256 256]
  :setup setup
  :draw draw
  :update update-scene
  :mouse-pressed click-press
  :features [:resizable]
  :middleware [qm/fun-mode])
