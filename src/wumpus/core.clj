(ns wumpus.core
  (:require [wumpus.game :as game]
            [wumpus.repl :as repl]))

(defn start-game! []
  (repl/game-repl))
