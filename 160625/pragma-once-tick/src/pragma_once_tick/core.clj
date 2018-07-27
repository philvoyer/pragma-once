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
   :time-lapse 0
   :timer-delay timer-delay-initial
   :timer-current 0
   :click-1 false
   :click-2 false})

(defn update-timer [state callback]
  (if (> (:timer-current state) (:timer-delay state))
    (do
      (callback)
      (mod (:timer-current state) (:timer-delay state)))
    (+ (:timer-current state) (:time-elapsed state))))

(defn update-scene [state]
  (assoc state
         :time-current (quil/millis)
         :time-elapsed (- (:time-current state) (:time-last state))
         :time-last (:time-current state)
         :timer-current (update-timer state tick)))

(defn draw [state]
  (quil/fill 0 7)
  (quil/rect 0 0 (quil/width) (quil/height))
  ;;
  (quil/fill 255)
  (quil/text-size 24)
  (quil/text-align :center :center)
  (quil/text (str "timer") (/ (quil/width) 2) (- (/ (quil/height) 2) 64))
  (quil/text (str (:time-current state)) (/ (quil/width) 2) (- (/ (quil/height) 2) 32))
  (quil/text (str (:timer-delay state)) (/ (quil/width) 2) (/ (quil/height) 2))
  (quil/text (str (:time-elapsed state)) (/ (quil/width) 2) (+ (/ (quil/height) 2) 32))
  (quil/text (str (:timer-current state)) (/ (quil/width) 2) (+ (/ (quil/height) 2) 64)))


(defn click-release [state event]
  (tick)
  ;; TODO on fisrt click pause, change color et draw timelapse
  ;; on second click reset timelapse and start
  (if (= (:click-1 state) true)
    (assoc state
            :timer-current 0
            :timer-delay (- (:time-current state) (:time-lapse state))
            :time-lapse (:time-current state))
    (assoc state
            :timer-current 0
            :timer-delay (- (:time-current state) (:time-lapse state))
            :time-lapse (:time-current state))
    ))

(quil/defsketch pragma-once-tick
  :title "#pragma-once-tick"
  :size [256 256]
  :setup setup
  :draw draw
  :update update-scene
  :mouse-released click-release
  :features [:resizable]
  :middleware [qm/fun-mode])
