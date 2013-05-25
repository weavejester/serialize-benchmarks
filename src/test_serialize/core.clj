(ns test-serialize.core
  (:require [criterium.core :as criterium]
            [carbonite.api :as carbonite]
            [carbonite.buffer]
            [deep-freeze.core :as deep-freeze]
            [taoensso.nippy :as nippy]
            [clojure.edn :as edn]
            [cheshire.core :as cheshire]))

(def test-data
  {:nil          nil
   :boolean      true
   :string-utf8  "ಬಾ ಇಲ್ಲಿ ಸಂಭವಿಸ"
   :string-long  (apply str (range 1000))
   :keyword      :keyword
   :ns-keyword   ::keyword
   :list         (list 1 2 3 4 5 (list 6 7 8 (list 9 10)))
   :list-quoted  '(1 2 3 4 5 (6 7 8 (9 10)))
   :list-empty   (list)
   :vector       [1 2 3 4 5 [6 7 8 [9 10]]]
   :vector-empty []
   :map          {:a 1 :b 2 :c 3 :d {:e 4 :f {:g 5 :h 6 :i 7}}}
   :map-empty    {}
   :set          #{1 2 3 4 5 #{6 7 8 #{9 10}}}
   :set-empty    #{}
   :coll         (repeatedly 1000 rand)
   :integer      (int 3)
   :long         (long 3)
   :bigint       (bigint 31415926535897932384626433832795)
   :float        (float 3.14)
   :double       (double 3.14)
   :bigdec       (bigdec 3.1415926535897932384626433832795)
   :ratio        22/7})

(def carbonite-registry
  (carbonite/default-registry))

(defn carbonite-write [data]
  (carbonite.buffer/write-bytes carbonite-registry data))

(defn deep-freeze-write [data]
  (deep-freeze/freeze-to-array data))

(defn nippy-write [data]
  (nippy/freeze-to-bytes data))

(defn cheshire-json-write [data]
  (cheshire/generate-string data))

(defn cheshire-smile-write [data]
  (cheshire/generate-smile data))

(defn clojure-write [data]
  (let [stream (java.io.ByteArrayOutputStream.)
        writer (java.io.BufferedWriter. (java.io.OutputStreamWriter. stream))]
    (binding [*out* writer] (pr data))
    (.flush writer)
    (.toByteArray stream)))


(def carbonite-read-data
  (carbonite-write test-data))

(def deep-freeze-read-data
  (deep-freeze-write test-data))

(def nippy-read-data
  (nippy-write test-data))

(def clojure-read-data
  (clojure-write test-data))

(def cheshire-json-read-data
  (cheshire-json-write test-data))

(def cheshire-smile-read-data
  (cheshire-smile-write test-data))


(defn carbonite-read [data]
  (carbonite.buffer/read-bytes carbonite-registry data))

(defn deep-freeze-read [data]
  (deep-freeze/thaw-from-array data))

(defn nippy-read [data]
  (nippy/thaw-from-bytes data))

(defn read-bytes [read-func data]
  (let [stream (java.io.ByteArrayInputStream. data)
        reader (java.io.BufferedReader. (java.io.InputStreamReader. stream))]
    (read-func (java.io.PushbackReader. reader))))

(defn clojure-core-read [data]
  (read-bytes read data))

(defn clojure-edn-read [data]
  (read-bytes edn/read data))

(defn cheshire-json-read [data]
  (cheshire/parse-string data))

(defn cheshire-smile-read [data]
  (cheshire/parse-smile data))


(defn report-mean [results]
  (format "%.2f µs" (* 1000000 (first (:mean results)))))

(defmacro bench-mean [form]
  `(report-mean (criterium/quick-benchmark ~form {})))

(defn -main []
  (binding [criterium/*final-gc-problem-threshold* 1.0]
    (println)
    (println "Testing Deep Freeze")
    (println "===================")
    (println "- write mean:" (bench-mean (deep-freeze-write test-data)))
    (println "- read mean: " (bench-mean (deep-freeze-read deep-freeze-read-data)))
    (println)
    (println "Testing Carbonite")
    (println "=================")
    (println "- write mean:" (bench-mean (carbonite-write test-data)))
    (println "- read mean: " (bench-mean (carbonite-read carbonite-read-data)))
    (println)
    (println "Testing Nippy")
    (println "=============")
    (println "- write mean:" (bench-mean (nippy-write test-data)))
    (println "- read mean: " (bench-mean (nippy-read nippy-read-data)))
    (println)
    (println "Testing Clojure reader")
    (println "======================")
    (println "- write mean:"      (bench-mean (clojure-write test-data)))
    (println "- core read mean: " (bench-mean (clojure-core-read clojure-read-data)))
    (println "- edn read mean: "  (bench-mean (clojure-edn-read clojure-read-data)))
    (println)
    (println "Testing Cheshire/JSON")
    (println "======================")
    (println "- write mean:"  (bench-mean (cheshire-json-write test-data)))
    (println "- read mean: "  (bench-mean (cheshire-json-read cheshire-json-read-data)))
    (println)
    (println "Testing Cheshire/SMILE")
    (println "======================")
    (println "- write mean:"  (bench-mean (cheshire-smile-write test-data)))
    (println "- read mean: "  (bench-mean (cheshire-smile-read cheshire-smile-read-data)))
    (println)))