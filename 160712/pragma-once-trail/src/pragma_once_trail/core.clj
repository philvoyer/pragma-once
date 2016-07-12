(ns pragma-once-trail.core
  (:require [quil.core :as quil]
            [quil.middleware :as qm]))

(def stroke-count 50)
(def stroke-radius-min 16)
(def stroke-radius-max 96)
(def stroke-decay 20)

(def stroke-shape (atom []))
(def stroke-position (atom []))

(defn draw-stroke [x y z r g b a]
  (quil/no-stroke)
  (quil/fill r g b a)
  (quil/ellipse-mode :center)
  (quil/ellipse x y z z))

(defn setup []
  (quil/frame-rate 60)
  (loop [index 1]
    (swap! stroke-shape
           conj {:alpha  (quil/map-range index 1 stroke-count 1 255)
                 :radius (quil/map-range index 1 stroke-count
                                         stroke-radius-min stroke-radius-max)})
    (if (< index stroke-count)
      (recur (inc index))))
  {:cursor-x 0 :cursor-y 0
   :press false :in-screen false
   :time {:last 0 :elapsed 0}
   :timer {:delay stroke-decay :current 0}})

(defn update-time [{:keys [last elapsed]} timestamp]
  {:last timestamp :elapsed (- timestamp last)})

(defn update-timer [{:keys [current delay]} timedelta]
  (if (> current delay)
    (do
      (swap! stroke-position (fn [current-state] (into [] (rest current-state))))
      {:current (mod current delay) :delay delay})
    {:current (+ current timedelta) :delay delay}))

(defn update-scene [state]
  (let [x (quil/mouse-x) y (quil/mouse-y)]
    (when (:in-screen state)
      (when (not= (peek @stroke-position) [x y])
        (if (< (count @stroke-position) stroke-count)
          (swap! stroke-position conj [x y])
          (do
            (swap! stroke-position (fn [current-state]
                                     (into [] (rest current-state))))
            (swap! stroke-position conj [x y])))))
    (assoc state
           :cursor-x x :cursor-y y
           :time  (update-time (:time state) (quil/millis))
           :timer (update-timer (:timer state) (:elapsed (:time state))))))

(defn draw [state]
  (if (:press state)
    (quil/background 0xAA)
    (quil/background 0x44))
  (when (> (count @stroke-position) 0)
    (loop [index 0]
      (let [position  (nth @stroke-position index)
            attribute (nth @stroke-shape index)]
        (draw-stroke
         (nth position 0) (nth position 1) (:radius attribute)
         255 0 55 (:alpha attribute)))
      (if (< index (- (count @stroke-position) 1))
        (recur (inc index))))))

(defn mouse-press [state event]
  (assoc state :press true))

(defn mouse-release [state event]
  (assoc state :press false))

(defn mouse-enter [state event]
  (assoc state :in-screen true))

(defn mouse-exit [state event]
  (assoc state :in-screen false))

(quil/defsketch pragma-once-trail
  :title  "#pragma-once-trail"
  :size  [256 256]
  :setup  setup
  :draw   draw
  :update update-scene
  :mouse-pressed  mouse-press
  :mouse-released mouse-release
  :mouse-entered  mouse-enter
  :mouse-exited   mouse-exit
  :features      [:resizable]
  :middleware    [qm/fun-mode])
