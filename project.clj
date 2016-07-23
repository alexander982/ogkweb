(defproject my-webapp "0.1.2-SNAPSHOT"
  :description "Поиск по базе данных ОГК"
  :url "http://192.168.0.132/"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [selmer "1.0.4"]
                 [hiccup "1.0.5"]
                 [metosin/ring-http-response "0.6.5"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [com.h2database/h2 "1.4.189"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-jetty-adapter "1.5.0"] 
                 ]
  :main my-webapp.handler
  :aot :all
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler my-webapp.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
