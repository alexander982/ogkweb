(ns my-webapp.db
  (:require [clojure.java.jdbc.deprecated :as sql]))

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2:file"
              :subname "~/doc/code/clojure/my-webapp/resources/db/ogkdb;IGNORECASE=true"
              :user "sa"})

(defn get-composition
  [prefix num]
  (let [results (sql/with-connection db-spec
                  (sql/with-query-results res
                    ["select c.pos, u.prefix, u.num, u.name, c.qnt from unit u, contain c where u.id = c.unit_id and c.cont_id in (select id from unit where prefix like ? and num like ?)  order by convert(c.pos,int)" prefix num]
                    (doall res)))]
    results))

(defn get-includes
  [prefix num]
  (let [results (sql/with-connection db-spec
                  (sql/with-query-results res
                    ["select prefix, num, name from unit where id in (select cont_id from contain where unit_id in  (select id from unit where prefix like ? and num like ?))" prefix num ]
                    (doall res)))]
    results))

(defn get-units-by-name
  [name]
  (let [results (sql/with-connection db-spec
                  (sql/with-query-results res
                    ["select prefix, num, name from unit where name like ?"
                     (str "%" name "%")]
                    (doall res)))]
    results))

(defn get-units-by-pref-num
  [pref num]
  (let [results (sql/with-connection db-spec
                  (sql/with-query-results res
                    ["select prefix, num, name from unit where prefix like ? and num like ?"
                     (str "%" pref "%")
                     (str "%" num "%")]
                    (doall res)))]
    results))

(defn get-units-by-num
  [num]
  (let [results (sql/with-connection db-spec
                  (sql/with-query-results res
                    ["select prefix, num, name from unit where num like ?"
                     (str "%" num "%")]
                    (doall res)))]
    results))
