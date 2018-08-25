(ns pragma-once-click.core
  (:require [quil.core :as quil]
            [quil.middleware :as qm]))

(defn draw-cross [x y]
  (quil/stroke-weight 4)
  (let [offset 8]
    (quil/point x y)
    (quil/point (+ x offset) y)
    (quil/point (- x offset) y)
    (quil/point x (+ y offset))
    (quil/point x (- y offset))))

(defn setup []
  (quil/smooth)
  (quil/frame-rate 60)
  (quil/no-fill)
  {:cursor-x 0
   :cursor-y 0
   :in-screen false
   :press false
   :click-history []})

(defn update-scene [state]
  (let [x (quil/mouse-x) y (quil/mouse-y)]
    (assoc state
           :cursor-x x
           :cursor-y y
           :click-history
           (if (:press state)
             (conj (state :click-history) [x y])
             (:click-history state)))))

(defn draw [state]

  ;; 1. draw background
  (if (:press state)
    (quil/background 0xAA)
    (quil/background 0x33))

  ;; 2. window outline
  (when (:in-screen state)
    (quil/stroke-weight 2)
    (quil/stroke 127)
    (quil/rect 0 0 (quil/width) (quil/height)))

  ;; 3. draw click history
  (quil/stroke-weight 3)
  (quil/stroke 127)
  (doseq [[x y] (:click-history state)]
    (draw-cross x y))

  ;; 4. draw mouse cursor
  (quil/stroke-weight 4)
  (quil/stroke 255 0 0)
  (draw-cross (:cursor-x state) (:cursor-y state)))

(defn click-press [state event]
  (println (str  "<click  press   at: " (:x event) "\t" (:y event) ">"))
  (assoc state :press true))

(defn click-release [state event]
  (println (str  "<click  release at: " (:x event) "\t" (:y event) ">"))
  (assoc state :press false))

(defn click-drag [state event]
  (println (str  "<click  drag    at: " (:x event) "\t" (:y event) ">"))
  (assoc state :press true))

(defn cursor-enter [state event]
  (println (str  "<cursor enter   at: " (:x event) "\t" (:y event) ">"))
  (assoc state :in-screen true))

(defn cursor-exit [state event]
  (println (str  "<cursor exit    at: " (:x event) "\t" (:y event) ">"))
  (assoc state :in-screen false))

(defn cursor-move [state event]
  (println (str  "<cursor move    at: " (:x event) "\t" (:y event) ">"))
  state)

(quil/defsketch pragma-once-click
  :title "#pragma-once-click"
  :size [256 256]
  :setup setup
  :draw draw
  :update update-scene
  :mouse-pressed click-press
  :mouse-released click-release
  :mouse-dragged click-drag
  :mouse-entered cursor-enter
  :mouse-exited cursor-exit
  :mouse-moved cursor-move
  :features [:resizable :no-bind-output]
  :middleware [qm/fun-mode])
