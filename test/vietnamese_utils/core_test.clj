(ns vietnamese-utils.core-test
  (:require [clojure.test :refer :all]
            [vietnamese-utils.core :refer :all]))

(deftest normalize-diacritics-tests
  (is (= (normalize-diacritics "huyền ảo")
         "huyền ảo"))
  (is (= (normalize-diacritics "huyền ảo" true)
         "huyền ảo"))
  (is (= (normalize-diacritics "tuỳ thuộc")
         "tuỳ thuộc"))
  (is (= (normalize-diacritics "tuỳ thuộc" true)
         "tùy thuộc")))
