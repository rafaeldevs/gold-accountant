#!/usr/bin/env bb


(require '[clojure.edn :as edn]
         '[clojure.java.io :as io])


(defn read-edn-file [filename]
  (with-open [reader (io/reader filename)]
    (edn/read (java.io.PushbackReader. reader))))


(def metals-prices (read-edn-file "prices.edn"))


(def metals  (read-edn-file "gold.edn"))


;; Calculates the total weight of the asset
(defn total-weight [quantity weight]
  (* quantity weight))


(defn ounces->dollar [ounces dollar-per-ounce]
  (* ounces dollar-per-ounce))


(defn quantity-weight-cost->value 
  [quantity weight dollar-per-ounce]
  (ounces->dollar dollar-per-ounce (total-weight quantity weight)))


;; Determines whether gold or silver and returns the value of the asset
(defn gold-silver-or
  [v mp]
  (cond
    (= (get v 4) :silver) (quantity-weight-cost->value (get v 0) (get v 1) (mp :us-silver) )
    (= (get v 4) :gold) (quantity-weight-cost->value (get v 0) (get v 1) (mp :us-gold) )
    :else "error"))


(def open-bag (map (fn [v] 
               
                [
                 
                 (total-weight 
                  (get v 0) ; quantity
                  (get v 1)) ; weight
                 
                 (gold-silver-or v metals-prices) 
                              
                 (get v 4) ; Metal (i.e Gold, Silver, ...)

                 (get v 2) ; Description

                 ]
                ) metals))


(defn -main [function] 
  (cond (= "help" function) (println "Available commands: list, total, help")
        (= "list" function) (doseq [ob open-bag] (println ob))
        (= "total" function) (println (str "$" (reduce (fn [acc v] (+ acc (gold-silver-or v metals-prices))) 0 metals)))

        ;; More functions to follow...

        ;; ...

        (= nil function) (println "Error. You must specify a function.")

        :else (println "Invalid command. Use 'help' for the list of available commands.")))


(-main (first *command-line-args*))