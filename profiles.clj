{:profiles/dev {:env
                {:database-url
                 "jdbc:h2:./ogkdb/ogkdb;IFEXISTS=TRUE;USER=sa"}}
 :profiles/test {:env
                 {:database-url
                  "jdbc:h2:./ogkdb/ogkdb;IFEXISTS=TRUE;USER=sa"}}}

