(ns lingo.view
  (:use hiccup.form
        [hiccup.def :only [defhtml]]
        [hiccup.element :only [link-to]]
        [hiccup.page :only [html5 include-css]])
  (:require [lingo.model :as model]))

(defhtml layout [& content]
  (html5
   [:head
    [:title "Lingo in clojure"]
    (include-css "/css/lingo.css")]
   [:body [:div#wrapper content]]))

(defn word-submit [] 
      (form-to [:post "/"]
           [:input {:name "word"
                    :type "text"
                    :id   "textbox"
                    :maxlength "5"}]
           [:input {:type   "submit"
                    :class  "verzendbutton"}]))

(defn cell-html [rownum colnum cell] 
  [:td 
   [:div {:name (str "b" rownum colnum) 
            :value (str cell)
            :class (if (= (second (str cell)) \[)
                     "tile correct" 
                     (if (= (second (str cell)) \])
                       "tile inword"
                       "tile"))}
    [:h1 (first(str cell))]]])

(defn row-html [rownum row]
  [:tr {:class "metrouicss row"}(map-indexed (fn [colnum cell]
                      (cell-html rownum colnum cell))
                    row)])

(defn board-html [board with-submit?]
           [:table 
            (map-indexed (fn [rownum row]
                           (row-html rownum row)) 
                         board)])

(defn play-screen []
  (layout
    [:div 
     [:p "Submit a 5 letter word!"]
     (word-submit)
     (board-html (model/get-board) true)]))



(defn winner-screen [winner]
  (layout
    [:div 
   [:p "The winning word is: " winner] 
   (board-html (model/get-board) false)
   (link-to "/" "Reset")]))

(defn draw-screen []
  (layout
    [:div
     [:p "You didn't guess right!"]
     (board-html (model/get-board) false)
     (link-to "/" "Reset")]))

