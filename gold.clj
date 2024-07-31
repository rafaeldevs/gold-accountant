#!/usr/bin/env bb

(require '[babashka.http-client :as http]
         '[cheshire.core :refer :all])


(defn get-url [url]
  (println "Downloading url:" url)
  (spit "XAU-USD-qoutex.json" (:body (http/get url))))

;; Next Steps
; - include calculation of records in which their weight is in grams
; - add documentation to the functions
; - add testing
; - wrap this functionality in babashka tasks


;; Now what is the equation, the formula, the function that shows me how much in pounds my gold and silver is worth?
;; If I convert that back into dollars is the same? 
;; If not then let enjoy my depth and breath as I take my fortune. Character, scripts, mind, soul, house, spirit, trees, plants, flowers, bring me fortune.


(def metals-prices {
                    
                    ;; :us-gold (get-url "https://forex-data-feed.swissquote.com/public-quotes/bboquotes/instrument/XAU/USD")
                    

                    ;; :us-gold 2441.40 ; Mon Jul 15 05:57:38 PM EDT 2024
                    ;; ;; :us-silver 30.86 ; Mon Jul 15 05:57:38 PM EDT 2024
                    ;; :us-silver 31.01 ; Mon Jul 15 10:51:50 PM EDT 2024

                    :us-gold 2431.40 ; Wed Jul 31 09:16:12 AM EDT 2024
                    :us-silver 28.86 ; Wed Jul 31 09:16:12 AM EDT 2024


})


;; works
;; (def to-json-file (spit "XAU-USD-qoutex.json" (:body (http/get "https://forex-data-feed.swissquote.com/public-quotes/bboquotes/instrument/XAU/USD"))))


(def from-json (cheshire.core/parse-string (slurp "XAU-USD-qoutex.json") true))

;; (println (get-url "https://forex-data-feed.swissquote.com/public-quotes/bboquotes/instrument/XAU/USD"))

;; (println (get (:body (http/get "https://forex-data-feed.swissquote.com/public-quotes/bboquotes/instrument/XAU/USD")) 6 ) )

;; (def runtime-cache (cheshire.core/parse-string (:body (http/get "https://forex-data-feed.swissquote.com/public-quotes/bboquotes/instrument/XAU/USD"))))
;; (println (get runtime-cache 0))

(println (type from-json))

(println "Gold:" (nth from-json 6)) ; https://www.reddit.com/r/webdev/comments/axkvck/comment/ehzf51u/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button

(println "Gold spread profile prices" (:spreadProfilePrices (nth from-json 6))) ; https://www.reddit.com/r/webdev/comments/axkvck/comment/ehzf51u/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button

(doseq [spp (:spreadProfilePrices (nth from-json 6))]
  (println spp))

;; (doseq [rc from-json]
;;   (println "rc:" rc))

;; (println (get from-json 1))

;; (println (get (:body (http/get "https://forex-data-feed.swissquote.com/public-quotes/bboquotes/instrument/XAU/USD")) 0 ) )

;; (doseq [b  (:body (http/get "https://forex-data-feed.swissquote.com/public-quotes/bboquotes/instrument/XAU/USD"))]
;;   (println b))

;; (println (cheshire.core/parse-string {:foo "bar" :baz {:eggplant [1 2 3]}} {:pretty true}))

;; The metal's vector may contain 0 or many unit-vectors. 
;; Unit-Vectors describe a bullion or coin in the following order:
;; quantity - weight-per-unit (ounces or grams) - Name - metal
(def metals
  [
[2 0.5 "American Double Eagle" :ounces :gold]
   [1 1 "American Double Eagle" :ounces :gold]
   [1 1 "Canadian Mapel Leaf" :ounces :gold]
   ;; [1 7 "Vespian" :grams :gold]
   [4 0.5 "Mexican Silver Libertad" :ounce :silver]
   [1 5 "Alphonse Mucha" :ounce :silver]
   [1 5 "Alphonse Mucha" :ounce :silver]
   [1 1 "American Silver Eagle" :ounce :silver]
   [6 1 "Apmex 1 ounce generic" :ounce :silver]
   [2 1 "Silver Britania" :ounce :silver]
   [1 1 "Mayan Silver Coin" :ounce :silver]
   [1 2 "The Kraken Silver" :ounce :silver]
   ])

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


(doseq [ob open-bag]
  (println ob))


(println "Total Metals:"  (str "$" (reduce (fn [acc v] (+ acc (gold-silver-or v metals-prices))) 0 metals)))





;;;; Notes
;;

;; equivalent to + in reduce because + take two argumnts.
;; Inside the reduce fucntion the first argument is the accumulator and the second is the value
;; (defn my-func [acc m] 
;;   (+ acc (gold-silver-or m)))
