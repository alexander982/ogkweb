{:profiles/dev {:env
                {:database-url
                 "jdbc:h2:./ogkdb/ogkdb;IFEXISTS=TRUE"}}}
 :profiles/test {:env
                 {:database-url
                  "jdbc:h2:/ogkdb/ogkdb;IFEXISTS=TRUE"}}}
