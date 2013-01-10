(ns PacmanAI_stu
	(:require [pacman.ghost :as ghost])
	(:use [pacman.paths :only [neighbours]]
           [clojure.set :only [union]]))

(comment System.Reflection.Assembly/LoadWithPartialName "System.Core")
(comment System.Reflection.Assembly/LoadWithPartialName "System.Drawing")
(System.Reflection.Assembly/LoadWithPartialName "PacManLib")

(import (PacManLib PlayerInfo+Direction IGhostAI IPacManAI Board Board+Item Log))



(comment def log [message]
  (let [file (System.IO.StreamWriter. "clj-ghost.log" true)]
    (.WriteLine file message)
    (.Close file)))

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
  [(/ (.X point) 1) (/ (.Y point) 1)])

(defn PlayerInfo->point [pi]
  (-> pi .Position point->vector))
  
(defn PlayerInfo->ghost [player-info]
  (let [position (-> player-info .Position point->vector)
        direction (-> player-info .CurrentDirection direction->clj)
        is-scared? (.IsScared player-info)]    
    {:pos position 
     :dir direction
     :scared is-scared?
     :id (.GhostID player-info)}))

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
		
(defn all-ghosts [board]
	 (->> board .Ghosts (map PlayerInfo->ghost)))

	 

(let [board (Board.)
	all (->> (Board.) accessible-cells (map first) set)
	dimensions [(.Width board) (.Height board)]]
	(def board-partial{
		:neighbours (memoize (fn[point](neighbours point dimensions)))
		:all all
		:log (fn [message] nil)
	}))
	
(defn board->clj [board]
	(assoc board-partial
		:pacman (pacman-cell board)
		:ghosts (all-ghosts board)
		:log (fn [message] nil)))
 
(def ghost-state (atom [13 23]))

(defn load-ghost-state-for [id]
  @ghost-state)

(defn save-state! [instance new-state]
  ;(swap! ghost-state conj {instance new-state}))
  (reset! ghost-state new-state))

(defn reset-state! []
  (save-state! nil #{[13 23]}))

(defn really-move-ghost [b ghostInfo state]
  (let [id (.GhostID ghostInfo)
        ;state (load-ghost-state-for id)
        board (board->clj b)
        ghost (PlayerInfo->ghost ghostInfo)
        [dir new-state] (ghost/move ghost board state)
        ]
        (save-state! id new-state)
        (clj->direction dir)))

(defn move-ghost [b ghostInfo]
    (let [id (.GhostID ghostInfo)
        state (load-ghost-state-for id)]
    
      (if (nil? state)      
        (do 
          (reset-state!)
          (clj->direction (if (odd? id) :left :right)))
        (really-move-ghost b ghostInfo state))))
