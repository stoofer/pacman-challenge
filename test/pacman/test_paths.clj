(ns pacman.test_paths
  (:use [midje.sweet]
        [pacman.paths]))

(fact "neighbouring cells on a board which wraps to and bottom"
 (neighbours [1 1] [3 3]) => { :left [0 1] :right [2 1] :down [1 0] :up [1 2]}
 (neighbours [0 0] [3 3]) => {:left [2 0] :right [1 0] :down [0 2] :up [0 1]})

(fact "filtering neighbours"
 (neighbours-in #(neighbours % [3 3]) [0 0] #{[2 0] [0 2] [-2 88]}) => #{[2 0] [0 2]})


(fact "filtering neighbours"
 (neighbours-of #(neighbours % [3 3]) [[0 0] [1 1]] #{[2 0] [0 1] [-2 88]}) => #{[2 0] [0 1]})

(tabular
 (fact "diffusion of scent through board"
       (diffuse #(neighbours % [100 100]) ?from ?value ?board) => ?results)
    ?from ?value ?board 						?results
    [0 0] 16     #{[0 0] [1 0] [2 0] [3 0] [4 0]}	{ [0 0] 16
 											[1 0]  8
 											[2 0]  4
 											[3 0]  2
 											[4 0]  1 }
 
    [2 0] 16     #{[0 0] [1 0] [2 0] [3 0] [4 0]}	{ [0 0]  4
 											[1 0]  8
 											[2 0] 16
 											[3 0]  8
 											[4 0]  4 })
