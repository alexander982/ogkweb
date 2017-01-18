--:name get-last-db-update :? :1
SELECT v_date FROM versions
WHERE v_id = (SELECT cv_id from cversion)