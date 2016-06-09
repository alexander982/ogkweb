(ns my-webapp.db
  (:require [clojure.java.jdbc :as sql]))

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2:file"
              :subname (str (System/getProperty "user.dir")
                            "\\ogkdb\\ogkdb;IGNORECASE=true")
              :user "sa"})

(defn get-unit-by-id
  [id]
  (first (sql/query db-spec
                    ["select * from unit where id = ?" id])))

(defn get-composition
  [prefix num]
  (sql/query db-spec
             [(str "select c.pos, u.prefix, u.num, u.name, c.qnt "
                   "from unit u, contain c "
                   "where u.id = c.unit_id and c.cont_id in "
                   "(select id from unit where prefix like ? and num like ?) "
                   "order by u.prefix, u.num")
              prefix num]))

(defn get-includes
  [prefix num]
  (sql/query db-spec
             [(str "select prefix, num, name "
                   "from unit where id in "
                   "(select cont_id from contain "
                   "where unit_id in "
                   "(select id from unit where prefix like ? and num like ?))")
              prefix num ]))

(defn get-units
  [pref num name]
  (sql/query db-spec
             [(str "select prefix, num, name from unit "
                   "where prefix like ? and num like ? and name like ?")
              (str "%" pref "%")
              (str "%" num "%")
              (str "%" name "%")]))

(defn get-diff
  [pref num1 num2]
  (sql/query db-spec
             [(str "select pref1, num1, name1, pref2, num2, name2, pos1, pos2 "
                   "from (select u2.prefix as pref1,"
                   "u2.num as num1, u2.name as name1,"
                   "c.pos as pos1 from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join unit u2 on c.unit_id = u2.id "
                   "where u.id = (select id from unit "
                   "where prefix like ? and num like ?) "
                   "minus "
                   "select u2.prefix, u2.num, u2.name, c.pos from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = (select id from unit "
                   "where prefix like ? and num like ?)) "
                   "left outer join "
                   "(select u2.prefix as pref2, u2.num as num2,"
                   "u2.name as name2, c.pos as pos2 "
                   "from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = (select id from unit "
                   "where prefix like ? and num like ?) "
                   "minus "
                   "select u2.prefix, u2.num, u2.name, c.pos from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = (select id from unit "
                   "where prefix like ? and num like ?)) "
                   "on pos1 = pos2 "
                   "union "
                   "select pref1, num1, name1, pref2, num2, name2, pos1, pos2 "
                   "from "
                   "(select u2.prefix as pref1, u2.num as num1, "
                   "u2.name as name1, c.pos as pos1 "
                   "from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = (select id from unit "
                   "where prefix like ? and num like ?) "
                   "minus "
                   "select u2.prefix, u2.num, u2.name, c.pos from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = (select id from unit "
                   "where prefix like ? and num like ?)) "
                   "right outer join "
                   "(select u2.prefix as pref2, u2.num as num2, "
                   "u2.name as name2, c.pos as pos2 "
                   "from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = (select id from unit "
                   "where prefix like ? and num like ?) "
                   "minus "
                   "select u2.prefix, u2.num, u2.name, c.pos from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = (select id from unit "
                   "where prefix like ? and num like ?)) "
                   "on pos1 = pos2")
              pref
              num1
              pref
              num2
              pref
              num2
              pref
              num1
              pref
              num1
              pref
              num2
              pref
              num2
              pref
              num1]))


(defn get-metals
  [pref num]
  (sql/query db-spec
             [(str  "with recursive sostav "
                    "(pref, num, name, qnt, gold, silver, pl, pal, id) as ("
                    "SELECT u2.prefix, u2.num, u2.name, c.qnt,"
                    " u2.gold, u2.silver, u2.pl, u2.pal, u2.id FROM "
                    "(UNIT u inner join contain c on u.id = c.cont_id ) "
                    "inner join unit u2 on u2.id  = c.unit_id "
                    "where u.id  = (select id from unit "
                    "where prefix like '" pref "' and num like '" num "') "
                    "union all "
                    "select u.prefix, u.num, u.name, c.qnt,"
                    "u.gold, u.silver, u.pl, u.pal, u.id "
                    "from ( sostav s inner join contain c on s.id = c.cont_id) "
                    "inner join unit u on c.unit_id = u.id) "
                    "select sum(cast(gold as double)*cast(qnt as int)) as gold,"
                    "sum(cast(silver as double)*cast(qnt as int)) as silver,"
                    "sum(cast(pl as double)*cast(qnt as int)) as pl,"
                    "sum(cast(pal as double)*cast(qnt as int)) as pal "
                    "from sostav")]))

(defn get-version-date
  []
  (sql/query db-spec
             [(str "select day(v_date) as day,"
                   "month(v_date) as month,"
                   "year(v_date) as year "
                   "from versions "
                   "where v_id = (select cv_id from cversion);")]))

(defn get-products
  [prefix name]
  (sql/query db-spec
             ["select cont_id, pref, name 
               from product where pref like ? and name like ?"
              (str "%" prefix "%")
              (str "%" name "%")]))

(defn get-composition-by-id
  [id]
  (sql/query db-spec
             ["select c.pos, u.prefix, u.num, u.name, c.qnt
               from unit u inner join contain c on u.id = c.unit_id
               where c.cont_id = ?
               order by convert(c.pos,int)"
              (Integer/parseInt id)]))
