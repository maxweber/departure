(ns departure.utils
  (:require [net.cgrand.enlive-html :as html])
  (:use [clojure.java.io :only [reader]]
        [clojure.test]))


(defn char-range [begin end]
     (let [begin (int begin)
           end (+ 1 (int end))]
       (map char (range begin end))))

(deftest char-range-test
  (is (= '(\A \B \C) (char-range \A \C))))

(defn fetch-url "Reads the data from a URL with the given
encoding (for example \"ISO-8859-1\") and parse it with enlive."
  [url encoding]
  (let [reader (reader url :encoding encoding)]
    (html/html-resource reader)))

(comment "Example how much lines of code and their for
complexity Clojure can hide compared to Java:
In Clojure (clojure.java.io) is enough to read from an URL with a specified encoding, when you say:
 
 (reader url :encoding \"ISO-8859-1\")

In Java you have to do something like this:
	BufferedReader in = new BufferedReader(
				new InputStreamReader(
				url.openStream(Charset.forName(\"ISO-8859-1\")));
In the Clojure case the same code works with a variety of objects and not only with an URL.
The Apache Commons IO solution is not really shorter than the pure Java one.")

