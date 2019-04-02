--:name backup-database :? :*
script nosettings drop
to 'backup.sql' table users, cversion, versions, params, unit_param;
