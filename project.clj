(defproject my-webapp "0.1.2-SNAPSHOT"
  :description "Поиск по базе данных ОГК"
  :url "http://192.168.0.132/"
  :min-lein-version "2.0.0"
  :dependencies [
                 [org.clojure/clojure "1.8.0"]
                 [selmer "1.0.4"]
                 [hiccup "1.0.5"]
                 [metosin/ring-http-response "0.6.5"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.clojure/tools.loggin "0.3.1"]
                 [org.clojure/tools.cli "0.3.5"]
                 [com.h2database/h2 "1.4.189"]
                 [compojure "1.5.0"]
                 [ring/ring-servlet "1.4.0"]
                 [ring/ring-defaults "0.2.1"]
                 [luminus-nrepl "0.1.4"]
                 [mount "0.1.10"]
                 [cprop "0.1.8"]
                 ]
  
  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :main my-webapp.core
  :plugins [[lein-uberwar "0.2.0"]
            [lein-cprop "1.0.1"]]
  
  :uberwar
  {:handler my-webapp.handler/app}
  
  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/rosources"]}
   
   :dev {:dependencies [[ring/ring-mock "0.3.0"]
                        [luminus-jetty "0.1.4"]
                        [directory-naming/naming-java "0.8"]]
         :repl-options {:init-ns user}
         :source-paths ["env/dev/clj"]
         :resource-paths ["env/dev/resources"]}})
