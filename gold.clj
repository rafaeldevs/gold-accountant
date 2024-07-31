#!/usr/bin/env bb


(defn read-edn-file [filename]
  (with-open [reader (io/reader filename)]
    (edn/read (java.io.PushbackReader. reader))))

;; (def data (read-edn-file "gold.edn"))

;; (prn data)

(def metals-prices (read-edn-file "prices.edn"))


;; (def metals-prices {
;;                     ;; :us-gold 2441.40 ; Mon Jul 15 05:57:38 PM EDT 2024
;;                     ;; :us-silver 31.01 ; Mon Jul 15 10:51:50 PM EDT 2024

;;                     :us-gold 2431.40 ; Wed Jul 31 09:16:12 AM EDT 2024
;;                     :us-silver 28.86 ; Wed Jul 31 09:16:12 AM EDT 2024
;; })

(def metals  (read-edn-file "gold.edn"))


;; (def metals
;;   [
;; [2 0.5 "American Double Eagle" :ounces :gold]
;;    [1 1 "American Double Eagle" :ounces :gold]
;;    [1 1 "Canadian Mapel Leaf" :ounces :gold]
;;    [4 0.5 "Mexican Silver Libertad" :ounce :silver]
;;    [1 5 "Alphonse Mucha" :ounce :silver]
;;    [1 5 "Alphonse Mucha" :ounce :silver]
;;    [1 1 "American Silver Eagle" :ounce :silver]
;;    [6 1 "Apmex 1 ounce generic" :ounce :silver]
;;    [2 1 "Silver Britania" :ounce :silver]
;;    [1 1 "Mayan Silver Coin" :ounce :silver]
;;    [1 2 "The Kraken Silver" :ounce :silver]
;;    ])



;; (require '[clojure.edn :as edn]
;;          '[clojure.java.io :as io])

;; (def data {:name "John Doe"
;;            :age 30
;;            :email "john.doe@example.com"})

;; (defn write-edn-file [filename data]
;;   (with-open [writer (io/writer filename)]
;;     (binding [*out* writer]
;;       (prn data))))

;; (write-edn-file "prices.edn" metals-prices)







(defn total-weight [quantity weight]
  (* quantity weight))

(defn ounces->dollar [ounces dollar-per-ounce]
  (* ounces dollar-per-ounce))

(defn quantity-weight-cost->value 
  [quantity weight dollar-per-ounce]
  (ounces->dollar dollar-per-ounce (total-weight quantity weight)))

(defn gold-silver-or
  "Determines whether gold or silver"
  [v mp]
  (cond
    (= (get v 4) :silver) (quantity-weight-cost->value (get v 0) (get v 1) (mp :us-silver) )
    (= (get v 4) :gold) (quantity-weight-cost->value (get v 0) (get v 1) (mp :us-gold) )
    :else "error"))


(def open-bag (map (fn [v] 
               
                [(total-weight ; Calculates the total weight of the line item
                  (get v 0) ; quantity
                  (get v 1)) ; weight
                 
                 (gold-silver-or v metals-prices) ; The value of the line item
                              
                 (get v 4) ; Metal (i.e Gold, Silver, ...)

                 (get v 2) ; Description
                 ]
                ) metals))


(defn -main [function] 
  (cond (= "help" function) (println "Available commands: list, total, help")
        (= "list" function) (doseq [ob open-bag] (println ob))
        (= "total" function) (println (str "$" (reduce (fn [acc v] (+ acc (gold-silver-or v metals-prices))) 0 metals)))

        ;; More functions to follow

        (= nil function) (println "Error. You must specify a function.")

        :else (println "Invalid command. Use 'help' for the list of available commands."))
  )

(-main (first *command-line-args*))