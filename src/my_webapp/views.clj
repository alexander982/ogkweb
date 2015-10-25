(ns my-webapp.views
  (:require [my-webapp.db :as db]
            [clojure.string :as str]
            [hiccup.page :as page]))


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
   [:a {:href "/about"} "About"]
   " ]"])

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
   [:h1 "Выборка данных на 30.09.2015г"]
   [:p "Данные не синхронизированы с основной базой и не содержат последних изменений!"]
   [:p [:a {:href "/cont_unit_req"}
        "Просмотр состава и входимости"]] 
   [:p [:a {:href "/search_by_pref_num"} "Поиск по обозначение и номеру"]]
   [:p [:a {:href "/search_by_name"} "Поиск по названию"]]
   [:p [:a {:href "/search_by_num"} "Поиск по номеру"]]))

(defn cont-unit-page
  [{:keys [pref num reqtype]}]
  (page/html5
   (gen-page-head "Поиск состава/входимости")
   header-links
   [:h1 "Введите данные"]
   (form-contain-unit-post pref num)
   (when pref
     [:div [:h1 "Результаты запроса"]
      [:p (str "Состав узла: " pref num)]
      [:table.result
       (let [cols ["Обозн." "Номер" "Название" "Кол." "Поз."]]
         (if (= reqtype  "s")
           (for [c cols] [:th c])
           (for [c (take 3 cols)] [:th c])))
       (for [compos (if (= reqtype "s")
                      (db/get-composition pref num)
                      (db/get-includes pref num))]
         [:tr {:onclick "insertToForm(this);"} [:td (:prefix compos)]
          [:td (:num compos)]
          [:td (:name compos)]
          (when (= reqtype "s")
            (list [:td (:qnt compos)]
              [:td (:pos compos)]))])]])))

(defn form-search-by-name
  [name]
  [:form {:action "/search_by_name" :method "POST"}
   [:p [:label "Наименование" [:input {:type "text"
                                       :name "name"
                                       :value name}]]]
   [:p [:input {:type "submit" :value "Поиск"}]]])

(defn search-by-name
  [{:keys [name]}]
  (page/html5
   (gen-page-head "Поиск узла/детали по названию")
   header-links
   [:h1 "Введите название"]
   (form-search-by-name name)
   (when name
     (list [:h1 "Или используйте полученные данные для поиска входимости/состава"]
           [:p "Можно кликнуть по строке для внесения данных."]
           (form-contain-unit-post nil nil)
           [:h2 (str "Рузультаты поиска " name)]
           [:table.result
            [:tr (for [c ["Обозначение" "Номер" "Название"]]
                   [:th c])]
            (for [r (db/get-units-by-name name)]
              [:tr {:onclick "insertToForm(this);"}
               [:td (:prefix r)]
               [:td (:num r)]
               [:td (:name r)]])]))))

(defn form-search-by-pref-num
  [pref num]
  [:form {:action "/search_by_pref_num" :method "POST"}
   [:table
    [:tr [:td "Обозначение:"] [:td [:input {:type "text"
                                            :name "pref"
                                            :value pref}]]]
    [:tr [:td "Номер:"] [:td [:input {:type "text"
                                      :name "num"
                                      :value num}]]]
    [:tr [:td [:input {:type "submit" :value "Найти"}]] [:td]]]])

(defn search-by-pref-num
  [{:keys [pref num]}]
  (page/html5
   (gen-page-head "Поиск по узла/детали по обозначению и номеру")
   header-links
   [:h1 "Введите обозначение и номер узла/детали"]
   [:p "Либо фирму и заказной номер покупного изделия."]
   (form-search-by-pref-num pref num)
   (when pref
     [:div
      [:h1 "Или введите обозначение и номер для поиска состава/входимости"]
      [:p "Левый клик мыши по строке таблицы вносит данные в форму запроса."]
      (form-contain-unit-post pref num)
      [:h2 (str "Результаты поиска: " pref num)]
      [:table.result
       [:tr (for [c ["Обозначение" "Номер" "Название"]]
              [:th c])]
       (for [r (db/get-units-by-pref-num pref num)]
         [:tr {:onclick "insertToForm(this);"}
          [:td (:prefix r)]
          [:td (:num r)]
          [:td (:name r)]])]])))

(defn form-search-by-num
  [num]
  [:form {:action "/search_by_num" :method "POST"}
   [:p [:label "Номер " [:input {:type "text"
                               :name "num"
                                 :value num}]]]
   [:p [:input {:type "submit" :value "Поиск"}]]])

(defn search-by-num
  [{:keys [num]}]
  (page/html5
   (gen-page-head "Поиск по номеру")
   header-links
   [:h1 "Введите номер детали или заказной номер покупного изделия"]
   (form-search-by-num num)
   (when num
     [:div
      [:h1 "Или введите данные для поиска состава/входимости"]
      [:p "Левый клик по строке таблицы вносит данные в форму запроса."]
      (form-contain-unit-post nil num)
      [:h2 (str "Рузультаты поиска номера: " num)]
      [:table.result
       [:tr
        (for [c ["Обозначение" "Номер" "Название"]]
          [:th c])
        (for [r (db/get-units-by-num num)]
          [:tr {:onclick "insertToForm(this);"}
           [:td (:prefix r)]
           [:td (:num r)]
           [:td (:name r)]])]]])))
