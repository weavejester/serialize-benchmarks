(ns test-serialize.core
  (:require [criterium.core :as criterium]
            [carbonite.api :as carbonite]
            [carbonite.buffer]
            [deep-freeze.core :as deep-freeze]
            [taoensso.nippy :as nippy]))

(def test-data
  {:nil          nil
   :boolean      true
   :char-utf8    \ಬ
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

(defn carbonite-write []
  (carbonite.buffer/write-bytes carbonite-registry test-data))

(defn deep-freeze-write []
  (deep-freeze/freeze-to-array test-data))

(defn nippy-write []
  (nippy/freeze-to-bytes test-data))

(defn pr-byte-array-write []
  (let [stream (java.io.ByteArrayOutputStream.)
        writer (java.io.BufferedWriter. (java.io.OutputStreamWriter. stream))]
    (binding [*out* writer] (pr test-data))
    (.toByteArray stream)))