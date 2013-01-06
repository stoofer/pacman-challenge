(ns pacman.ghost
  (:use [clojure.set :only [difference]])
  (:require [pacman.paths :as path]))

(defn move[{:keys [pos dir]} {:keys [pacman empty neighbours ghosts] :as board}]
  (let [ghost-cells (into #{} (map :pos ghosts))
        cells (difference empty ghost-cells)
        scent (path/diffuse neighbours pacman 1024 cells)
        choices (neighbours pos)
        score-direction (fn[[dir pt]](list (scent pt) dir))
        ]
    (->> choices 
         (map score-direction)
         (sort-by first)
         reverse
         first
         second)))