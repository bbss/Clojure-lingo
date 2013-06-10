(ns lingo.controller
  (:use compojure.core)
  (:require [compojure.core :as compojure]
            [lingo.view :as view]
            [lingo.model :as model]))

(defn start-page []
  (model/reset-game!)
  (view/play-screen))

(defn turn-page [submit]
  (model/play! (:word submit))
    (if-let [winner (model/winner? (:word submit))]
        (view/winner-screen winner)
        (if (model/full-board?)
          (view/draw-screen)
          (view/play-screen))))

(defroutes tictactoe-routes
  (GET "/" [] (start-page))
  (POST "/" {button-pressed :params} (turn-page button-pressed)))
