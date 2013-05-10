(defproject test-serialize "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ptaoussanis/deep-freeze "1.2.1"]
                 [org.xerial.snappy/snappy-java "1.0.5-M4"]
                 [com.taoensso/nippy "1.2.0"]
                 [com.twitter/carbonite "1.3.2"]
                 [criterium "0.4.1"]]
  :main test-serialize.core)
