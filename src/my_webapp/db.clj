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
             [(str "select c.pos, u.id, u.prefix, u.num, u.name, c.qnt "
                   "from unit u, contain c "
                   "where u.id = c.unit_id and c.cont_id in "
                   "(select id from unit where prefix like ? and num like ?) "
                   "order by convert(c.pos,int)")
              prefix num]))

(defn get-includes
  [prefix num]
  (sql/query db-spec
             [(str "select id, prefix, num, name "
                   "from unit where id in "
                   "(select cont_id from contain "
                   "where unit_id in "
                   "(select id from unit where prefix like ? and num like ?))")
              prefix num ]))

(defn get-occurrence-by-id
  [id]
  (sql/query db-spec
             [(str "select id, prefix, num, name "
                   "from unit where id in "
                   "(select cont_id from contain "
                   "where unit_id = ?)")
              (Integer/parseInt id)]))

(defn get-units
  [pref num name]
  (sql/query db-spec
             [(str "select id, prefix, num, name from unit "
                   "where prefix like ? and num like ? and name like ?")
              (str "%" pref "%")
              (str "%" num "%")
              (str "%" name "%")]))

(defn get-diff-by-ids
  [id1 id2]
  (sql/query db-spec
             [(str "select pref1, num1, name1, qnt1, pref2, num2, name2, qnt2, pos1, pos2 "
                   "from (select u2.prefix as pref1,"
                   "u2.num as num1, u2.name as name1,"
                   "c.pos as pos1, c.qnt as qnt1 from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join unit u2 on c.unit_id = u2.id "
                   "where u.id = ? "
                   "minus "
                   "select u2.prefix, u2.num, u2.name, c.pos, c.qnt from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = ? )"
                   "left outer join "
                   "(select u2.prefix as pref2, u2.num as num2,"
                   "u2.name as name2, c.pos as pos2, c.qnt as qnt2 "
                   "from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = ? "
                   "minus "
                   "select u2.prefix, u2.num, u2.name, c.pos, c.qnt from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = ?) "
                   "on pos1 = pos2 "
                   "union "
                   "select pref1, num1, name1, qnt1, pref2, num2, name2, qnt2, pos1, pos2 "
                   "from "
                   "(select u2.prefix as pref1, u2.num as num1, "
                   "u2.name as name1, c.pos as pos1, c.qnt as qnt1 "
                   "from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = ? "
                   "minus "
                   "select u2.prefix, u2.num, u2.name, c.pos, c.qnt from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = ?)"
                   "right outer join "
                   "(select u2.prefix as pref2, u2.num as num2, "
                   "u2.name as name2, c.pos as pos2, c.qnt as qnt2 "
                   "from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = ? "
                   "minus "
                   "select u2.prefix, u2.num, u2.name, c.pos, c.qnt from "
                   "(unit u inner join contain c on u.id = c.cont_id) "
                   "inner join "
                   "unit u2 on c.unit_id = u2.id "
                   "where u.id = ? )"
                   "on pos1 = pos2")
              id1 id2
              id2 id1
              id1 id2
              id2 id1]))

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

(defn get-metals-by-id
  [id]
  (sql/query db-spec
             [(str  "with recursive sostav "
                    "(pref, num, name, qnt, gold, silver, pl, pal, id) as ("
                    "SELECT u2.prefix, u2.num, u2.name, c.qnt,"
                    " u2.gold, u2.silver, u2.pl, u2.pal, u2.id FROM "
                    "(UNIT u inner join contain c on u.id = c.cont_id ) "
                    "inner join unit u2 on u2.id  = c.unit_id "
                    "where u.id  = " id
                    " union all "
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
             [(str "select v_date "
                   "from versions "
                   "where v_id = (select cv_id from cversion);")]))

(defn get-products
  [prefix name]
  (sql/query db-spec
             ["select cont_id, pref, name 
               from product where pref like ? and (name like ? or name is null)"
              (str "%" prefix "%")
              (str "%" name "%")]))

(defn get-composition-by-id
  [id]
  (sql/query db-spec
             ["select c.pos, u.id, u.prefix, u.num, u.name, c.qnt
               from unit u inner join contain c on u.id = c.unit_id
               where c.cont_id = ?
               order by convert(c.pos,int)"
              (Integer/parseInt id)]))

(defn get-plan-years
  []
  (sql/query db-spec
             ["select distinct year from plan order by year desc"]))

(defn get-plans
  [year quarter month]
  (sql/query db-spec
             [(str "select unit_id, model, m"
                   month " as qnt"
                   " from plan where year = " year
                   " and quarter = " quarter
                   " and  m" month " <>0 order by kpprod")]))

(defn get-docs-by-fname
  [fname]
  (sql/query db-spec
             ["select * from docs where fname like ?"
              (str "%" fname "%")]))

(defn get-doc-by-id
  [id]
  (sql/query db-spec
             ["select * from docs where id = ?" id]))
