(ns my-webapp.views
  (:require [my-webapp.db :as db]
            [clojure.string :as str]
            [hiccup.page :as page])
  (:gen-class))


(defn gen-page-head
  [title]
  [:head
   [:title title]
   (page/include-css "/css/styles.css")
   (page/include-js "/js/table.js")])

(def header-links
  [:div#header-links
   "[ "
   [:a {:href "/"} "Главная"]
   " | "
   [:a {:href "/cont_unit_req"} "Поиск состава/входимоси"]
   " | "
   [:a {:href "/search"} "Поиск узла/детали"]
   " | "
   [:a {:href "/diff"} "Просмотр различий"]
   " | "
   [:a {:href "/metals"} "Просмотр содержания драгметаллов"]
   " | "
   [:a {:href "http://192.168.0.132:8080/wiki/ru/"} "Вики"]
   " ]"])

(defn form-hidden
  [pref num]
  [:div#hidenForms
     [:table [:tr
       [:td [:form {:action "/cont_unit_req" :method "POST"}
         [:input {:type "hidden" :name "pref" :value pref}]
         [:input {:type "hidden" :name "num" :value num}]
         [:input {:type "hidden" :name "reqtype" :value "s"}]
         [:input {:type "submit" :value "   Состав   "}]]]
       [:td [:form {:action "/cont_unit_req" :method "POST"}
         [:input {:type "hidden" :name "pref" :value pref}]
         [:input {:type "hidden" :name "num" :value num}]
         [:input {:type "hidden" :name "reqtype" :value "v"}]
         [:input {:type "submit" :value "Входимость"}]]]]]])

(defn form-contain-unit-post
  [pref num]
  [:form {:action "/cont_unit_req" :method "POST"}
   [:table
    [:tr [:td "Обозначение:"] [:td [:input {:type "text"
                                            :name "pref"
                                            :value pref}]]]
    [:tr [:td "Номер:"] [:td [:input {:type "text"
                                      :name "num"
                                      :value num}]]]
    [:tr [:td [:label
               [:input {:type "radio" :name "reqtype"
                        :value "s" :checked "on"}]
               "Состав"]] [:td]]
    [:tr [:td [:label
               [:input {:type "radio" :name "reqtype" :value "v"}]
               "Входимость"]] [:td]]
    [:tr [:td [:input {:type "submit" :value "Найти"}]] [:td]]]])

(defn home-page
  []
  (page/html5
   (gen-page-head "Home")
   header-links
   [:h1 "Выборка данных на 03.11.2015г"]
   [:p "Данные не синхронизированы с основной базой и не содержат последних изменений!"]
   [:p [:a {:href "/cont_unit_req"}
        "Просмотр состава и входимости"]] 
   [:p [:a {:href "/search"} "Поиск детали/узла/комплектующих"]]
   [:p [:a {:href "/diff"} "Просмотр различий исполнений"]]
   [:p [:a {:href "/metals"} "Просмотр драгметаллов"]]
   [:h1 "Вики"]
   [:p [:a {:href "http://192.168.0.132:8080/wiki/ru/"} "Вики"]
    " позволяет накапливать различные знания. Не стоит надеяться на свою память сохраните все " [:a {:href "http://192.168.0.132:8080/wiki/ru/"} "здесь!"]]))

(defn cont-unit-page
  [{:keys [pref num reqtype]}]
  (page/html5
   (gen-page-head "Поиск состава/входимости")
   header-links
   (form-hidden pref num)
   [:h1 "Введите данные"]
   (form-contain-unit-post pref num)
   (when pref
     [:div [:h1 "Результаты запроса"]
      [:p (str (if (= reqtype "s")
                 "Состав узла: "
                 "Входимость: ") pref num)]
      [:table.result
       (let [cols ["Обозн." "Номер" "Название" "Кол." "Поз."]]
         (if (= reqtype  "s")
           (for [c cols] [:th c])
           (for [c (take 3 cols)] [:th c])))
       (for [compos (if (= reqtype "s")
                      (db/get-composition pref num)
                      (db/get-includes pref num))]
         [:tr {:onclick "insertToForm(this, event);"} [:td (:prefix compos)]
          [:td (:num compos)]
          [:td (:name compos)]
          (when (= reqtype "s")
            (list [:td (:qnt compos)]
              [:td (:pos compos)]))])]])))

(defn form-search
  [pref num name]
  [:form {:action "/search" :method "POST"}
   [:table
    [:tr [:td "Обозначение:"] [:td [:input {:type "text"
                                            :name "pref"
                                            :value pref}]]]
    [:tr [:td "Номер:"] [:td [:input {:type "text"
                                      :name "num"
                                      :value num}]]]
    [:tr [:td "Название:"] [:td [:input {:type "text"
                                         :name "name"
                                         :value name}]]]
    [:tr [:td [:input {:type "submit" :value "Найти"}]]
     [:td ]]]])

(defn search-page
  [{:keys [pref num name]}]
  (page/html5
   (gen-page-head "Поиск узла/детали")
   header-links
   (form-hidden pref num)
   [:h1 "Введите данные"]
   [:p "Допускается не заполнять все поля"]
   (form-search pref num name)
   (when (or pref num name)
     [:div
      [:h2 (str "Результаты запроса: " pref num " " name)]
      [:table.result
       [:tr (for [c ["Обозначение" "Номер" "Название"]]
              [:td c])]
       (for [r (db/get-units pref num name)]
         [:tr {:onclick "insertToForm(this, event);"}
          [:td (:prefix r)]
          [:td (:num r)]
          [:td (:name r)]])]])))

(defn form-diff
  [pref num1 num2]
  [:form {:action "/diff" :method "POST"}
   [:table
    [:tr [:td "Обозначение:"] [:td [:input {:type "text"
                                            :name "pref"
                                            :value pref}]]]
    [:tr [:td "Номер 1:"] [:td [:input {:type "text"
                                        :name "num1"
                                        :value num1}]]]
    [:tr [:td "Номер 2:"] [:td [:input {:type "text"
                                        :name "num2"
                                        :value num2}]]]
    [:tr [:td [:input {:type "submit" :value "Найти"}]] [:td ]]]])

(defn diff-page
  [{:keys [pref num1 num2]}]
  (page/html5
   (gen-page-head "Просмотр различий")
   header-links
   [:h1 "Введите данные"]
   (form-diff pref num1 num2)
   (when (or pref num1 num2)
     [:div
      [:h2 (str "Отличие " pref num1 " от " pref num2)]
      [:table.result
       [:tr (for [c ["Обозначение" "Номер" "Название"]]
              [:th c])]
       (for [r (db/get-diff pref num1 num2)]
         [:tr [:td (:prefix r)]
          [:td (:num r)]
          [:td (:name r)]])]
      [:h2 (str "Отличие " pref num2 " от " pref num1)]
      [:table.result
       [:tr (for [c ["Обозначение" "Номер" "Название"]]
              [:th c])]
       (for [r (db/get-diff pref num2 num1)]
         [:tr [:td (:prefix r)]
          [:td (:num r)]
          [:td (:name r)]])]])))

(defn form-rare-metals
  [pref num]
  [:form {:action "/metals" :method "POST"}
   [:table
     [:tr [:th] [:th] [:th "Пример"]]
     [:tr [:td "Обозначение: "]
      [:td [:input {:type "text"
                    :name "pref"
                    :value pref}]]
      [:td "ОШ-525Ф3"]]
     [:tr [:td "Номер: "]
      [:td [:input {:type "text"
                    :name "num"
                    :value num}]]
      [:td ".00.0.000.0.00-04"]]
     [:tr [:td [:input {:type "submit" :value "Посчитать"}]]]]])

(defn metals-page
  [{:keys [pref num]}]
  (page/html5
   (gen-page-head "Просмотр драгметаллов")
   header-links
   [:h1 "Введите данные"]
   (form-rare-metals pref num)
   (when (or pref num)
     [:div
      [:h2 (str "Содержание драгметаллов в: " pref num)]
      [:table.result
       [:tr (for [c ["Золото" "Серебро" "Платина" "Палладий"]]
              [:th c])]
       (for [r (db/get-metals pref num)]
         [:tr [:td (:gold r)]
          [:td (:silver r)]
          [:td (:pl r)]
          [:td (:pal r)]])]])))
