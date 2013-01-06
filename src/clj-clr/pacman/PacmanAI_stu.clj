(ns pacman.PacmanAI_stu
	(:require [pacman.ghost :as ghost])
	(:use [pacman.paths :only [neighbours]]))

(System.Reflection.Assembly/LoadWithPartialName "System.Core")
(System.Reflection.Assembly/LoadWithPartialName "System.Drawing")
(System.Reflection.Assembly/LoadWithPartialName "PacManLib")

(import (PacManLib PlayerInfo+Direction IGhostAI IPacManAI Board Board+Item))

(def me "Stuart Caborn")

(defn clj->direction [kw]
	(let [dirmap {  :no-change (PlayerInfo+Direction/NoChange)
					:left (PlayerInfo+Direction/Left)
					:right (PlayerInfo+Direction/Right)
					:up (PlayerInfo+Direction/Up)
					:down (PlayerInfo+Direction/Down)}]	
		(dirmap kw)))
		
(defn direction->clj [direction]
	(let [dirmap { "NoChange" :no-change, "Left" :left, "Right" :right, "Up" :up, "Down" :down}]
		(dirmap (.ToString direction))))
		
(defn board-item->clj [item]
	(let [items {  "Wall" :wall "Pellet" :dot "PowerPellet" :pill "Empty" :empty }]
		(items (.ToString item))))

(defn point->vector [point]
  [(.X point) (.Y point)])

(defn PlayerInfo->point [pi]
  (-> pi .Position point->vector))
  
(defn PlayerInfo->ghost [pi]
  (let [pos (-> pi .Position point->vector)
		dir (-> pi .CurrentDirection direction->clj)
		scared (.IsScared pi)]
	{:pos pos :dir dir :scared scared}))
		
(defn accessable? [content]
	(not (= :wall content)))

(defn content-of [items [x y]] 
 (board-item->clj (.GetValue items x y)))
 
(defn matching-cells [board condition]
  (let [items (.Items board)]
	(into {} 
		(for [x (range (.Width board)) 
	      y (range (.Height board))
		  :when (condition (content-of items [x y]))] 
		  {[x y] (content-of items [x y])}))))
		  
(defn accessible-cells [board]
  (matching-cells board accessable?))
  
(defn pacman-cell [board]
  (let [cell-or-unknown (point->vector (.. board PacMan Position))
		unknown (point->vector (Board/UnknownPacmanPosition))]
	(if (= unknown cell-or-unknown)
		:unknown
		cell-or-unknown)))
		
(defn ghost-cells [board]
	 (->> board .Ghosts (map PlayerInfo->point) set))

	
(defn board->clj [board]
	(let [cells (accessible-cells board)
	      all-of (fn[type]
					(->> cells
						 (filter (fn[[_ content]] (= type content)))
						 (map first)
						 set))
		  empty-cells (all-of :empty)
		  pills (all-of :pill)
		  dots (all-of :dot)
		  dimensions [(.Width board) (.Height board)]]
		{
			:pacman (pacman-cell board)
			:ghosts (ghost-cells board)
			:dots dots 
			:pills pills 
			:neighbours (fn[point](neighbours point dimensions))
			:empty empty
		}))
 
  
(defrecord LazyGhost []
	IGhostAI
    (getAIName [_]
       "Lazy Ghost")
	   
	(getDesiredDirection [this b ghostInfo]
		(let [board(board->clj b)
              ghost (PlayerInfo->ghost ghostInfo)
			  dir (ghost/move ghost board)]
			(clj->direction dir)))
	   
	(getCodersName [_] me "Stuart Caborn"))
	   
(defrecord DozyPac []
	IPacManAI
    (getAIName [_]
       "Dozy Pac")
	   
	(getDesiredDirection [this board]
       (PlayerInfo+Direction/NoChange))
	   
	(getCodersName [_] me))
