(ns pragma-once-background.core
  (:require [quil.core :as quil]
            [quil.middleware :as qm]))

(defn random-color-rgb [color-range]
  (into [] (take 3 (repeatedly #(rand-int color-range)))))

(defn setup []
  (quil/frame-rate 60)
  {:clear-color (random-color-rgb 256) :press false})

(defn update-scene [state]
  (if (:press state)
    (assoc state :clear-color (random-color-rgb 256))
    state))

(defn draw [state]
  (let [[r g b] (:clear-color state)]
    (quil/background r g b)))

(defn click-press [state event]
  (assoc state :press true))

(defn click-release [state event]
  (assoc state :press false))

(quil/defsketch pragma-once-background
  :title "#pragma-once-background"
  :size [256 256]
  :setup setup
  :draw draw
  :update update-scene
  :mouse-pressed click-press
  :mouse-released click-release
  :features [:resizable :no-bind-output]
  :middleware [qm/fun-mode])
