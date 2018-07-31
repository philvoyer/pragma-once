(ns pragma-once-canvas.core
  (:require [quil.core :as quil]
            [quil.middleware :as qm]))

(defn setup []
  (quil/frame-rate 60)
  {:press false})

(defn update-scene [state]
  state)

(defn draw [state]
  (if (:press state)
    (quil/background 255)
    (quil/background 127)))

(defn click-press [state event]
  (assoc state :press true))

(defn click-release [state event]
  (assoc state :press false))

(quil/defsketch pragma-once-canvas
  :title  "#pragma-once-canvas"
  :size  [256 256]
  :setup  setup
  :draw   draw
  :update update-scene
  :mouse-pressed  click-press
  :mouse-released click-release
  :features      [:resizable]
  :middleware    [qm/fun-mode])
