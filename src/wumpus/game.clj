(ns wumpus.game
  (:require [clojure.set :as set]))

(def parameters
  "Static parameters that determine the game world.
  These are taken straight from the book."
  {:node-num 30
   :edge-num 45
   :worm-gangs 3
   :cop-odds 1/15})

;;;;; Graph creation

(defn random-node
  "Returns a random node from nodes"
  [nodes]
  (rand-nth nodes))

(defn edge-pair
  "Given a pair of nodes, return a pair of edges between them, one in either
  direction."
  [a b]
  (when-not (= a b)
    #{a b}))

(defn make-edge-list
  "Returns a random list of num (pairs of) edges connecting random nodes."
  [nodes num-edges]
  (->> (repeatedly (partial random-node (into [] nodes)))
       (partition 2)
       (map (partial apply edge-pair))
       (remove nil?)
       distinct
       (take num-edges)))

;;;;; Make sure the city is connected

(defn direct-edges
  "Returns edges incident on the given node."
  [node edge-list]
  (filter #(contains? % node) edge-list))

(defn traverse
  "Helper for get-connected."
  [node visited edge-list]
  (let [visited (conj visited node)]
    (into visited
          (comp
           (map second)
           (remove #(contains? visited %))
           (mapcat #(traverse % visited edge-list)))
          (direct-edges node edge-list))))

(defn get-connected
  "Given a node. returns the set of all nodes reachable from that node in the
  graph."
  [node edge-list]
  (traverse node #{} edge-list))

(defn find-islands
  "Given a graph in the form of a set of nodes and a list of edges, return all
  connected subsets of the node set."
  [nodes edge-list]
  (let [connected (get-connected (first nodes) edge-list)
        unconnected (set/difference nodes connected)]
    (if (empty? unconnected)
      #{connected}
      (conj (find-islands unconnected edge-list) connected))))

(defn connect-with-bridges
  "Given a set of islands, return a list of edges which when added to the graph
  will make it fully connected."
  [[i1 i2 & more]]
  (when i2
    (conj (connect-with-bridges (conj more i2))
            (edge-pair (first i1) (first i2)))))

(defn connect-all-islands
  "Given a set of nodes and a list of edges, returns a new list of edges which
  make a graph with no islands"
  [nodes edge-list]
  (concat (connect-with-bridges (find-islands nodes edge-list)) edge-list))

;;;;; Generate a city


(defn edgelist->map [edges]
  (merge (group-by first edges)
         (group-by second edges)))

(defn maybe-add-cops [odds x]
  (if (< (rand) odds)
    (with-meta x {:cops true})
    x))

(defn make-city-edges [parameters]
  (let [nodes (into #{} (map inc (range (:node-num parameters))))
        edge-list (connect-all-islands
                   nodes (make-edge-list nodes (:edge-num parameters)))]
    (map (partial maybe-add-cops (:cop-odds parameters)) edge-list)))

(defn within-1? [x y])

(defn within-2? [x y])

(defn make-city-nodes [places edges parameters]
  (let [wumpus     (random-node places)
        edge-map   (edgelist->map edges)
        glow-worms (->> (repeatedly (partial random-node places))
                        (take (:worm-gangs parameters))
                        (into #{}))]

    (map (fn [node]
           (cond-> {:name node}
             (= node wumpus)                              (assoc :wumpus true)
             (within-2? node wumus)                       (assoc :blood true)
             (contains? glow-worms node)                  (assoc :glow-worm true)
             (some #(within-1? node %) glow-worms)        (assoc :lights true)
             (some #(:cops (meta x)) (get edge-map node)) (assoc :sirens true)))
         nodes)))
