(ns pacman.paths
  (:use [clojure.set :only [intersection union difference]]))
		
(defn neighbours [[x y] [width height]]
  (let [translate (fn[[desc [dx dy]]]
                    {desc [(mod (+ x dx) width) (mod (+ y dy) height)]})
        options { :left [-1 0]
                  :right [1 0]
                  :up [0 -1]
                  :down [0 1] }]
       (into {} (map translate options))))

(defn opposite [direction]
  (direction {:left :right, :right :left, :up :down, :down :up}))

(defn neighbours-in [n-fn point valid-neighbours]
  (let [possibles(into #{} (vals (n-fn point)))]
   (intersection possibles valid-neighbours)))

(defn moves-in [n-fn point valid-neighbours log]
  (let [possibles (n-fn point)
        ;_ (log possibles)
        filtered (into {} (filter (fn[[k v]] (not (nil? (valid-neighbours v)))) possibles))]
    (comment log filtered)
    filtered))

(defn neighbours-of [n-fn points valid-neighbours]
  (apply union (map #(neighbours-in n-fn % valid-neighbours) points)))

(defn diffuse 
  ([n-fn cell value into-cells]
   (if (set? cell)
     (diffuse n-fn (seq cell) value (difference into-cells cell) (into {} (map #(vector % value) cell)))
     (diffuse n-fn [cell] value (difference into-cells #{cell}) {cell value})))
  
  ([n-fn cells value remaining done]
   (if (empty? remaining)
     done
     (let [neighbours (neighbours-of n-fn cells remaining)
           next-value (/ value 2)
           next-remaining (difference remaining neighbours)
           next-done (into done (map #(vector % next-value) neighbours))]
       (if (= next-done done)
         (into next-done (map #(vector % 0) next-remaining));orphans score zero
         (recur n-fn neighbours next-value next-remaining next-done))))))