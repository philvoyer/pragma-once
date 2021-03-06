(ns pragma-once-tick.core
  (:require [quil.core :as quil]
            [quil.middleware :as qm]))

(def timer-delay-initial 1000)
(def timer-delay-min 1)
(def timer-delay-max 5000)

(defn tick []
  (quil/background 255))

(defn setup []
  (quil/frame-rate 60)
  (quil/no-stroke)
  {:time-current 0
   :time-last 0
   :time-elapsed 0
   :time-stamp 0
   :timer-delay timer-delay-initial
   :timer-current 0})

(defn update-timer [state callback]
  (let [time-new (+ (:timer-current state) (:time-elapsed state))]
    (if (> time-new (:timer-delay state))
      (do
        (callback)
        (mod time-new (:timer-delay state)))
      time-new)))

(defn update-scene [state]
  (assoc state
         :time-current (quil/millis)
         :time-elapsed (- (:time-current state) (:time-last state))
         :time-last (:time-current state)
         :timer-current (update-timer state tick)))

(defn draw [state]
  (quil/fill 0 7)
  (quil/rect 0 0 (quil/width) (quil/height)))

(defn click-release [state event]
  (tick)
  (assoc state
         :timer-current 0
         :timer-delay (min (- (:time-current state) (:time-stamp state)) timer-delay-max)
         :time-stamp (:time-current state)))

(quil/defsketch pragma-once-tick
  :title "#pragma-once-tick"
  :size [256 256]
  :setup setup
  :draw draw
  :update update-scene
  :mouse-released click-release
  :features [:resizable :no-bind-output]
  :middleware [qm/fun-mode])
