(ns pacman.test_ghost
  (:use midje.sweet
        [pacman.paths :only [neighbours]]
        [pacman.ghost :only [move]]))

        
(tabular
    (fact "movement when pacman position is known (ignoring cannot turn round rule)"
      (let [ghost {:pos ?ghost-pos :dir ?ghost-dir }
            board { :pacman ?pacman
                    :ghosts (map (fn[p]{:pos p}) (seq ?other-ghosts))
                    :neighbours (fn[point](neighbours point [10 10]))
                    :empty ?cells }
            direction (move ghost board)]
        direction => ?new-dir))
    ?ghost-pos ?ghost-dir ?pacman ?new-dir ?other-ghosts ?cells
    [0 0]      :up        [1 0]   :right   []            #{[0 0] [1 0]}
    [2 0]      :up        [1 0]   :left    []            #{[0 0] [1 0] [2 0]}
    [0 0]      :right     [2 0]   :up      []            #{[0 1] [1 1] [2 1]
                                                           [0 0]       [2 0]} 
    [0 0]      :right     [2 0]   :up      [[1 0]]       #{[0 1] [1 1] [2 1]
                                                           [0 0] [1 0] [2 0]} 

 )

(future-fact "the ghost cannot turn around unless scared")
(future-fact "the ghost runs form pacman when scared")
(future-fact "the ghost updates pacman last known position as state when known")
(future-fact "the ghost guesses pacman possible position when not known")
(future-fact "the ghost guesses pacman possible position when not known and scared")
