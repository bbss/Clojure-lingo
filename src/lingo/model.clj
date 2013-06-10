(ns lingo.model
  (:require [noir.session :as session]))

(defn get-word [] "lingo")

(def init-board [[(str(clojure.string/upper-case(first(get-word)) )\[) \- \- \- \-]
                  [\- \- \- \- \-]
                  [\- \- \- \- \-]
                  [\- \- \- \- \-]
                  [\- \- \- \- \-]])

(def init-state {:board init-board :the-word get-word :turn 0})

(defn get-board []
  (:board (session/get :game-state)))

(defn get-board-letter
  ([row col]
    (get-board-letter row col (get-board)))
  ([row col board]
    (get-in board [row col])))

(defn contains-char? [the-char, the-string]
         (some #(= (.charAt (clojure.string/lower-case the-char) 0) %) (clojure.string/lower-case the-string)))

(defn is-in-word? [letter the-word] (contains-char? letter the-word))

(defn is-in-correct-place? [located-letter the-word]
  (let [[char x] located-letter]
    (= (str (.charAt (clojure.string/lower-case the-word) x)) 
       (clojure.string/lower-case char))))

(defn split-word [word] 
  (drop 1 (clojure.string/split word #"")))

(defn combine-into-located-letters [split-word] (map vector split-word [0 1 2 3 4]))

(defn correct-letters [guess the-word] 
  (for [x (combine-into-located-letters guess)]
    (is-in-correct-place? x the-word)))

(defn letters-in-word [guess the-word]
  (for [[char x] (combine-into-located-letters guess)]
    (is-in-word? char the-word)))

(defn word-with-correctness [word]
       (let [guess (apply map vector 
              [(split-word word)
               (correct-letters word (get-word))
               (letters-in-word word (get-word))])]
         (for [x (range 5)] (str 
                        (first (nth guess x))
                        (if (second (nth guess x))
                          "[")
                        (if (nth (nth guess x) 2)
                          "]")
                        ))))

(defn winner? 
  ([guess] (winner? guess (get-word)))
  ([guess word] (winner? (clojure.string/capitalize guess) (clojure.string/capitalize word) (get-board)))
  ([guess word board] (if (= guess word)
                        word
                        false)))

(defn reset-game! [] 
  (session/put! :game-state init-state))

(defn new-state [word old-state]
  (if (and 
        (not (= (:turn old-state) 5))
        (not (winner? word))
        (not (< (count word) 5)))
    {:board (assoc (:board old-state)
                   (:turn old-state)
                    (word-with-correctness word))     
     :turn (inc (:turn old-state))}
    old-state))

(defn play! [word]
  (session/swap! (fn [session-map]
                   (assoc  session-map :game-state 
                          (new-state word (:game-state session-map))))))

(defn full-board?
  ([] (full-board? (get-board)))
  ([board] (let [all-cells (apply concat board)]
             (not-any? #(= % \-) all-cells))))

