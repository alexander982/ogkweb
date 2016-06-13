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
   [:a {:href "/cont_unit_req"} "Просмотр состава/входимости"]
   " | "
   [:a {:href "/search"} "Поиск узла/детали"]
   " | "
   [:a {:href "/diff"} "Просмотр различий"]
   " | "
   [:a {:href "/metals"} "Просмотр драгметаллов"]
   " | "
   [:a {:href "/products"} "Просмотр продукции"]
   " | "
   [:a {:href "http://192.168.0.132/wiki/ru/"} "Вики"]
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
    [:tr [:td] [:td] [:td "Пример:"]]
    [:tr [:td "Обозначение:"] [:td [:input {:type "text"
                                            :name "pref"
                                            :value pref}]]
     [:td "ОШ-450"]]
    [:tr [:td "Номер:"] [:td [:input {:type "text"
                                      :name "num"
                                      :value num}]]
     [:td ".81.2.000.0.00"]]
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
   (gen-page-head "Сайт ОГК")
   header-links
   [:div {:align "center"}
    [:h1 "База ОГК"]
    (let [[{:keys [day month year]}] (db/get-version-date)]
      [:h2 "Выборка данных на " [:u (str day "." month "." year "!")]])
    [:p "Данные не синхронизированы с основной базой и не содержат
последних изменений!"]
    [:p [:a {:href "/cont_unit_req"}
         "Просмотр состава и входимости"]]
    [:p [:a {:href "/search"} "Поиск детали/узла/комплектующих"]]
    [:p [:a {:href "/diff"} "Просмотр различий исполнений"]]
    [:p [:a {:href "/metals"} "Просмотр драгметаллов"]]
    [:p [:a {:href "/products"} "Просмотр продукции/изделий"]]
    [:h1 "Вики"]
    [:p [:a {:href "http://192.168.0.132/wiki/ru/"} "Вики"]
     " - сайт, содержание которого можно менять."]]))

(defn cont-unit-page
  [{:keys [pref num reqtype cont-id]}]
  (page/html5
   (gen-page-head "Поиск состава/входимости")
   header-links
   (form-hidden pref num)
   [:div#reqForm {:align "center"}
    [:h1 "Введите данные"]
    (form-contain-unit-post pref num)]
   (when (or cont-id pref num)
     [:div#resultFrame {:align "center"}
      [:h1 "Результаты запроса "
       [:button {:title "Показать/скрыть остальное"
                 :onclick "showOnlyTable(this)"} "^"]]
      [:h2 (str (if (= reqtype "s")
                  "Состав узла: "
                  "Входимость: ")
                (if ((complement nil?) cont-id)
                  (let [{:keys [prefix num]} (db/get-unit-by-id cont-id)]
                    (str prefix num))
                  (str pref num)))]
      [:table.result
       (let [cols ["Обозн." "Номер" "Название" "Кол." "Поз."]]
         (let [head (if (= reqtype  "s")
                      (for [c cols] [:th c])
                      (for [c (take 3 cols)] [:th c]))]
           (conj (conj (first head)
                       [:button {:onclick "joinColumns();"
                                 :title "Объединить с соседней"} "<"])
                 (rest head))))
       (for [compos (if (= reqtype "s")
                      (if cont-id
                        (db/get-composition-by-id cont-id)
                        (db/get-composition pref num))
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
    [:tr [:td] [:td] [:td "Пример:"]]
    [:tr [:td "Обозначение:"] [:td [:input {:type "text"
                                            :name "pref"
                                            :value pref}]]
     [:td "ОШ-450"]]
    [:tr [:td "Номер:"] [:td [:input {:type "text"
                                      :name "num"
                                      :value num}]]
     [:td ".81.2.000.0.00"]]
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
   [:div {:align "center"}
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
           [:td (:name r)]])]])]))

(defn form-diff
  [pref num1 num2]
  [:div {:align "center"}
   [:form {:action "/diff" :method "POST"}
    [:table
     [:tr [:td] [:td] [:td "Пример:"]]
     [:tr [:td "Обозначение:"] [:td [:input {:type "text"
                                             :name "pref"
                                             :value pref}]]
      [:td "ОШ-450"]]
     [:tr [:td "Номер 1:"] [:td [:input {:type "text"
                                         :name "num1"
                                         :value num1}]]
      [:td ".81.2.000.0.00"]]
     [:tr [:td "Номер 2:"] [:td [:input {:type "text"
                                         :name "num2"
                                         :value num2}]]
      [:td ".81.2.000.0.00-01"]]
     [:tr [:td [:input {:type "submit" :value "Найти"}]] [:td ]]]]])

(defn diff-page
  [{:keys [pref num1 num2]}]
  (page/html5
   (gen-page-head "Просмотр различий")
   header-links
   [:div {:align "center"}
    [:h1 "Введите данные"]
    (form-diff pref num1 num2)
    (when (or pref num1 num2)
      [:div
       [:h2 (str "Отличие " pref num1 " от " pref num2)]
       [:table.result
        [:tr [:th {:colspan "3"} (str pref num1)]
         [:th {:colspan "3"} (str pref num2)]]
        [:tr (for [c ["Обозначение" "Номер" "Название"
                      "Обозначение" "Номер" "Название"]]
               [:th c])]
        (for [r (db/get-diff pref num1 num2)]
          [:tr [:td (:pref1 r)]
           [:td (:num1 r)]
           [:td (:name1 r)]
           [:td (:pref2 r)]
           [:td (:num2 r)]
           [:td (:name2 r)]])]])]))

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
   [:div {:align "center"}
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
           [:td (:pal r)]])]])]))

(defn form-products
  [pref name]
  [:form {:action "/products" :method "POST"}
   [:table
    [:tr [:th] [:th] [:th "Пример:"]]
    [:tr [:td  "Модель:"]
     [:td [:input {:type "text"
                   :name "pref"
                   :value pref}]]
     [:td "ОШ-525Ф3*01"]]
    [:tr [:td "Название:"]
     [:td [:input {:type "text"
                   :name "name"
                   :value name}]]
     [:td "П/А круглошлифовальный"]]
    [:tr [:td [:input {:type "submit" :value "Найти"}]]]]])

(defn form-contain-hiden
  []
  [:form#hidenForms {:action "/cont_unit_req" :method "POST"}
   [:input {:type "hidden" :name "cont-id"}]
   [:input {:type "hidden" :name "reqtype" :value "s"}]
   [:input {:type "submit" :value "Состав"}]])

(defn products-page
  [{:keys [pref name]}]
  (page/html5
   (gen-page-head "Просмотр продукции")
   header-links
   #_(form-contain-hiden)
   [:div {:align "center"}
    [:h1 "Введите данные"]
    (form-products pref name)
    (when (or pref name)
      [:div
       [:h2 (str "Результаты поиска " pref " " name)]
       [:table.result
        [:tr [:th "Модель"] [:th "Наименование"] [:th "Действие"]]
        (for [r (db/get-products pref name)]
          [:tr {:onclick "insertToForm(this, event);"}
           [:td (:pref r)]
           [:td (:name r)]
           [:td [:a {:href (str "/cont_unit_req?reqtype=s&cont-id="
                                (:cont_id r))}
                 "Состав"]]])]])]))
