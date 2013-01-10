(ns pacman.test_ghost
  (:use midje.sweet
        [pacman.paths :only [neighbours]]
        [pacman.ghost :only [move]]))

        
(tabular
    (fact "movement when pacman position is known normal ghost"
      (let [ghost {:pos ?ghost-pos :dir ?ghost-dir :scared false :id 1}
            board { :log println
                    :pacman ?pacman
                    :ghosts (map (fn[p]{:pos p :dir :right :id 0}) (seq ?other-ghosts))
                    :neighbours (fn[point](neighbours point [10 10]))
                    :all ?cells }
            [direction _] (move ghost board #{})]
        direction => ?new-dir))
    ?ghost-pos ?ghost-dir ?pacman ?new-dir ?other-ghosts ?cells
    [0 0]      :up        [1 0]   :right   []            #{[0 0] [1 0]}
 
    [2 0]      :up        [1 0]   :left    []            #{[0 0] [1 0] [2 0]}
    [0 0]      :right     [2 0]   :down    []            #{[0 1] [1 1] [2 1]
                                                           [0 0]       [2 0]} 
    ;Ghost in the way
    [0 0]      :right     [2 0]   :down    [[1 0]]       #{[0 1] [1 1] [2 1]
                                                           [0 0] [1 0] [2 0]} 
    ;Ghost with lower id on same square alrady made best choice
    [0 0]      :right     [2 0]   :down    [[0 0]]       #{[0 1] [1 1] [2 1]
                                                           [0 0] [1 0] [2 0]} 
    ;The ghost cannot turn around unless scared
    [0 0]      :left      [2 0]   :down    []            #{[0 1] [1 1] [2 1]
                                                           [0 0] [1 0] [2 0]} 
 )
(tabular
    (fact "the ghost runs from pacman when scared, can turn around and run through other ghosts"
      (let [ghost {:pos ?ghost-pos :dir ?ghost-dir :scared true}
            board { :log println
            :pacman ?pacman
                    :ghosts (map (fn[p]{:pos p}) (seq ?other-ghosts))
                    :neighbours (fn[point](neighbours point [10 10]))
                    :all ?cells }
            [direction _] (move ghost board #{})]
        direction => ?new-dir))
    ?ghost-pos ?ghost-dir ?pacman ?new-dir ?other-ghosts ?cells
    [0 0]      :right     [1 0]   :down      []            #{[0 0] [1 0] [0 1]}
    [1 0]      :up        [0 0]   :right   []            #{[0 0] [1 0] [2 0]}
    [2 1]      :right     [2 0]   :left    []            #{[0 1] [1 1] [2 1]
                                                           [0 0]       [2 0]}
    [1 0]      :up        [2 0]   :left    [[0 0]]       #{[0 0] [1 0] [2 0]})


(fact "the ghost always updates pacman last known position as state when known"
      (let [ghost {:pos [0 0] :dir :left}
            board { :log println
            :pacman [2 0]
                    :ghosts {}
                    :neighbours (fn[point](neighbours point [10 10]))
                    :all #{} }]
            (second (move ghost board nil)) => #{[2 0]}
            (second (move (conj ghost {:scared true}) board nil)) => #{[2 0]}))

(tabular
  (fact "the ghost guesses pacman possible position when not known"
        (let [ghost {:pos [0 0] :dir :left}
              board { :log println
              :pacman :unknown
                    :ghosts {}
                    :neighbours (fn[point](neighbours point [5 5]))
                    :all ?cells }]
            (second (move ghost board ?old-state)) => ?new-state))
 ?cells                       ?old-state    ?new-state
 #{ [0 3] [1 3] [2 3] [3 3]
    [0 2] [1 2] [2 2] [3 2]
    [0 1] [1 1] [2 1] [3 1]
    [0 0] [1 0] [2 0] [3 0]}  #{[2 2]}   #{[1 2] [2 2] [3 2] [2 1] [2 3]}
 
 #{ [0 3] [1 3] [2 3] [3 3]
    [0 2] [1 2] [2 2] [3 2]
    [0 1] [1 1] [2 1] [3 1]
    [0 0] [1 0] [2 0] [3 0]}  #{[3 3]}   #{[2 3] [3 3] [3 2]}
 
 #{ [0 3] [1 3] [2 3] [3 3]
    [0 2] [1 2] [2 2] [3 2]
    [0 1] [1 1] [2 1] [3 1]
    [0 0] [1 0] [2 0] [3 0]}  #{[2 3] [3 3] [3 2]} #{[1 3] [2 2] [3 1] [2 3] [3 3] [3 2]} )

(future-fact "the ghost guesses pacman possible position when not known and scared")
