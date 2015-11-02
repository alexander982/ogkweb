(defproject my-webapp "0.1.0-SNAPSHOT"
  :description "Поиск по базе данных"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [com.h2database/h2 "1.4.189"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-jetty-adapter "1.4.0"] 
                 ]
  :main my-webapp.handler
  :aot :all
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler my-webapp.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
