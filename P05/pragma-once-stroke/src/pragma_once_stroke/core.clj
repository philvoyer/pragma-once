(ns pragma-once-stroke.core
  (:require [quil.core :as quil]
            [quil.middleware :as qm]))

(def stroke-radius 32)
(def stroke-alpha 63)
(def clear-color 0x44)

(defn draw-stroke [x y z r g b a]
  (quil/no-stroke)
  (quil/fill r g b a)
  (quil/ellipse-mode :center)
  (quil/ellipse x y z z))

(defn random-color-rgb [color-range]
  (into [] (take 3 (repeatedly #(rand-int color-range)))))

(defn setup []
  (quil/frame-rate 60)
  (quil/background clear-color)
  {:stroke-color [255 255 255]
   :cursor-x 0
   :cursor-y 0
   :press false})

(defn update-scene [state]
  (let [x (quil/mouse-x) y (quil/mouse-y)]
    (assoc state
           :cursor-x x
           :cursor-y y)))

(defn draw [state]
  (when (:press state)
    (let [x (:cursor-x state)
          y (:cursor-y state)
          r (nth (:stroke-color state) 0)
          g (nth (:stroke-color state) 1)
          b (nth (:stroke-color state) 2)]
      (draw-stroke x y stroke-radius r g b stroke-alpha))))

(defn click-press [state event]
  (assoc state :press true))

(defn click-release [state event]
  (assoc state
         :press false
         :stroke-color (random-color-rgb 256)))

(defn key-release [state event]
  (quil/background clear-color)
  state)

(quil/defsketch pragma-once-stroke
  :title "#pragma-once-stroke"
  :size [256 256]
  :setup setup
  :draw draw
  :update update-scene
  :mouse-pressed click-press
  :mouse-released click-release
  :key-released key-release
  :features [:resizable :no-bind-output]
  :middleware [qm/fun-mode])
