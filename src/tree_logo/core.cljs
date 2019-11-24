(ns tree-logo.core
  (:require  [clojure.string :as s]
             [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def to-rad (partial * (/ Math/PI 180)))

(defn sin [deg]
  (Math/sin (to-rad deg)))

(defn cos [deg]
  (Math/cos (to-rad deg)))

(defn abs [v]
  (if (pos? v) v (* -1 v)))

(defonce app-state (atom {:stroke 2
                          :angle  60
                          :growth 0.6
                          :depth  5}))

(defn polyline [[[x1 y1] [x2 y2] :as points] k]
  (let [x   (- x1 x2)
        y   (- y1 y2)
        len (Math/sqrt (+ (* x x) (* y y)))]
    [:polyline
     {:key          k
      :fill         "none"
      :stroke       "black"
      :stroke-width (/ len (:stroke @app-state))
      :points       (s/join " " (map (fn [[x y]] (str x "," y)) points))}]))

(defn line [[[x y] length deg]]
  [[x y]
   [(+ x (* (sin deg) length))
    (+ y (* (cos deg) length))]])

(defn next-line [[[_ _] length deg :as prev] op]
  (let [{:keys [angle growth]} @app-state
        [[_ _] [x y]]          (line prev)]
    [[x y] (* length growth) (op deg angle)]))

(defn tree
  ([]
   (tree [[0 0] 100 -180] 1))
  ([trunk depth]
   (if (> depth (:depth @app-state))
     [trunk]
     (let [prev-line trunk]
       [trunk [(tree (next-line prev-line +) (inc depth))
               (tree (next-line prev-line -) (inc depth))]]))))

(defn branches [[trunk [left right]]]
  (concat [(line trunk)]
          (when left (branches left))
          (when right (branches right))))

(defn svg-component []
  (let [lines  (branches (tree))
        points (mapcat identity lines)
        padding 10
        min-x  (apply min (map first points))
        min-y  (apply min (map last points))
        max-x  (apply max (map first points))
        max-y  (apply max (map last points))]
    [:svg {:xmlns   "http://www.w3.org/2000/svg"
           :version "1.1"
           :width   800
           :height  800
           :viewBox #_"-20 -20 40 40"
           (str (- (min min-x min-y)
                   padding) " "
                (- (min min-x min-y)
                   padding) " "
                (+ (abs (min min-x min-y))
                   (max max-x max-y)
                   (* padding 2)) " "
                (+ (abs (min min-x min-y))
                   (max max-x max-y)
                   (* padding 2)))}
     [:g
      (doall
       (map (fn [points i] (polyline points i))
            lines
            (iterate inc 0)))]]))

(defn input-range [name path attrs]
  (let [val (get-in @app-state path)]
    [:label
     [:span (str " " name " (" val ")")]
     [:input (merge {:type      "range"
                     :value     val
                     :on-change #(swap! app-state assoc-in path (-> % .-target .-value js/parseFloat))}
                    attrs)]]))

(defn hello-world []
  [:main
   [:div.controls
    (input-range "Stroke" [:stroke] {:min 1 :max 10 :step 0.1})
    (input-range "Angle" [:angle] {:min 1 :max 90 :step 1})
    (input-range "Growth" [:growth] {:min 0.1 :max 1 :step 0.01})
    (input-range "Depth" [:depth] {:min 1 :max 10 :step 1})]
   [svg-component]])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))
