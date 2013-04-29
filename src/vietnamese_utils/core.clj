(ns vietnamese-utils.core
  (:require [clojure.string :as str])
  (:import [java.text Normalizer]))

(def NFC  java.text.Normalizer$Form/NFC)
(def NFD  java.text.Normalizer$Form/NFD)
(def NFKC java.text.Normalizer$Form/NFKC)
(def NFKD java.text.Normalizer$Form/NFKD)

(def ^:private tones "([\u0300\u0309\u0303\u0301\u0323])")

(defn normalize-diacritics
  [s & [classic?]]
  (let [fn-convert-to-classic-on-demand
        (if classic?
          #(str/replace
            %
            (re-pattern (str "(?i)(?<!q)([ou])([aeoy])" tones "(?!\\w)" ))
            "$1$3$2")
          identity)]
    (-> s
        (Normalizer/normalize NFD)
        (str/replace
         (re-pattern (str "(?i)" tones "([aeiouy\u0306\u0302\u031B]+)"))
         "$2$1")
        (str/replace
         (re-pattern (str "(?i)(?<=[\u0306\u0302\u031B])(.)"
                          tones "\\B")) ;; or "\\b" if Java <= 1.5
         "$2$1")
        (str/replace
         (re-pattern (str "(?i)(?<=[ae])([iouy])"
                          tones))
         "$2$1")
        (str/replace
         (re-pattern (str "(?i)(?<=[oy])([iuy])"
                          tones))
         "$2$1")
        (str/replace
         (re-pattern (str "(?i)(?<!q)(u)([aeiou])"
                          tones))
         "$2$1")
        (str/replace
         (re-pattern (str "(?i)(?<!g)(i)([aeiouy])"
                          tones))
         "$2$1")
        fn-convert-to-classic-on-demand
        (Normalizer/normalize NFC))))

;; Works around an obscure Normalization bug which
;; erroneously converts D with stroke and d with stroke to D and d,
;; respectively, on certain Windows systems,
;; by substituting them with \00DO and \00F0, respectively,
;; prior to normalization and then reverting them in post-processing.

(defn normalize-diacritics-compat
  [s & [classic?]]
  (-> s
      (str/replace \u0110 \u00D0)
      (str/replace \u0111 \u00F0)
      (normalize-diacritics classic?)
      (str/replace \u00D0 \u0110)
      (str/replace \u00F0 \u0111)))
