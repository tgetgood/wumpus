(ns wumpus.game)

(def parameters
  "Static parameters that determine the game world.
  These are taken straight from the book."
  {:node-num 30
   :edge-num 45
   :worm-gangs 3
   :cop-odds 1/15})

(defn random-edge [n]
  (let [a (rand-int n)
        b (rand-int n)]
    (if (= a b)
      (random-edge n)
      [a b])))

(defn create-world [parameters]
  (let [areas (range 0 (parameters :node-num))
        roads (into #{}) 
                    (map (fn [_] (random-edge (parameters :node-num))) 
                    (range 0 (parameters :edge-num)))])))
