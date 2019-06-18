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

(defn edge-graph
  "Create a hash map of the edges, e.g. {1 #{4 3 2}, 2 #{3}, 3 #{4}}"
  [edges]
  (into {}
        (map (fn [[k v]]
               [k (into #{} (map second v))])
             (group-by first edges)))

(defn depth-first-search
  "Create a list of list of edges by islands, e.g. [[[1 2] [2 3]] [[5 6] [6 7]]]"
  [nodes edges]
  (let [graph (group-by first edges)]
    (loop [visited #{}
           components []]
      (if (= nodes visited)
        components
        ()))))

(defn connect-graph [areas roads]
  (let [connected-areas (depth-first-search areas roads)
        root-nodes (map (comp first first) connected-areas)
        extra-edges (map vector root-nodes (drop 1 root-nodes))]
    extra-edges
    ))

(defn create-world [params]
  (let [n (:node-num params)
        e (:edge-num params)
        areas (into #{} (range n))
        roads (into #{} (take e (repeatedly #(random-edge n))))
        ]
    ))
