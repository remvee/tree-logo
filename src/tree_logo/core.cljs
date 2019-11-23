(ns tree-logo.core
  (:require  [clojure.string :as s]
             [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(def one-rad (/ Math/PI 180))

(defn sin [deg]
  (Math/sin (* one-rad deg)))

(defn cos [deg]
  (Math/cos (* one-rad deg)))

(defn abs [v]
  (if (pos? v) v (* -1 v)))

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn polyline [[[x1 y1] [x2 y2] :as points] k]
  (let [x   (- x1 x2)
        y   (- y1 y2)
        len (Math/sqrt (+ (* x x) (* y y)))]
    [:polyline
     {:key          k
      :fill         "none"
      :stroke       "black"
      :stroke-width (/ len 2)
      :points       (s/join " " (map (fn [[x y]] (str x "," y)) points))}]))

(def vs (map (fn [v] [[0 0] 100 v])
             (range 1 360 20)))

(defn line [[[x y] length deg]]
  [[x y]
   [(+ x (* (sin deg) length))
    (+ y (* (cos deg) length))]])

(defn next-line [[[_ _] length deg :as prev] op]
  (let [[[_ _] [x y]]      (line prev)]
    [[x y] (* length 0.6) (op deg 60)]))

(defn tree
  ([]
   (tree [[0 0] 100 -180] 1))
  ([trunk depth]
   (if (> depth 5)
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
      (map (fn [points i] (polyline points i))
           lines
           (iterate inc 0))]]))

(defn hello-world []
  [:div
   [svg-component]])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
