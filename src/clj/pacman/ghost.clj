(ns pacman.ghost
  (:use [clojure.set :only [difference]]
        [clojure.pprint])
  (:require [pacman.paths :as path]))

(defn testing [say]
  (println say))

(def pac-scent 1024)

(defn chase[{:keys [pos dir id]} {:keys [all pacman neighbours ghosts log] :as board} pac-pos]
  (let [ghost-cells (into #{} (map :pos ghosts))
        cells (difference all ghost-cells)
        scent (path/diffuse neighbours pac-pos pac-scent cells)
        accessible (path/moves-in neighbours pos all log)
        legal-directions (dissoc accessible (path/opposite dir))
        
        _ (log (str "facing:" dir "|legal:" legal-directions))
        already-moved-where-I-am (fn [{
                                      other-id :id 
                                      other-pos :pos}] 
                                   (and (> id other-id) 
                                        (= pos other-pos)))
        
        directions-in-use (->> ghosts
                               (filter already-moved-where-I-am)
                               (map :dir))
        
        _ (log (str "in-use:" (vector directions-in-use)))
        choices (apply dissoc legal-directions directions-in-use)
        
        _ (log (str "choices:" choices))
        score-direction (fn[[dir pt]](list (scent pt) dir))
        _ (log (str "score-direction:" (vector (map score-direction choices))))
        
        choice (->> choices 
                 (map score-direction)
                 (sort-by first)
                 reverse
                 first
                 second)]
    (log (str "chose:" choice))
    (or choice :no-change)))

(defn run-away[{:keys [pos dir]} {:keys [all neighbours] :as board} pac-pos]
  (let [cells (conj all pos)
        scent (path/diffuse neighbours pac-pos pac-scent cells)
        choices (neighbours pos)
        score-direction (fn[[dir pt]](list (scent pt) dir))]
    (->> choices 
         (map score-direction)
         (sort-by first)
         (remove #(nil? (first %)))
         first
         second)))

(defn expand [state neighbours valid-cells]
  state)
  ;(into state (path/neighbours-of neighbours state valid-cells)))

(defn possible-locations [{:keys [pacman neighbours all] :as board} state]
  (if (= :unknown pacman)
    (expand state neighbours all)
    #{pacman}))
  
(defn move[{:keys [scared] :as ghost} {:keys [pacman] :as board} state]
  (let [pac-pos (possible-locations board state)
        ;_ (println (str "state:" state "|scared:" scared "|pac-pos" pac-pos))
        ]
    (if scared
      [(run-away ghost board pac-pos) pac-pos]
      [(chase ghost board pac-pos) pac-pos])))