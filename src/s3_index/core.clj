(ns s3-index.core
  (:gen-class)
  (require [amazonica.aws.s3]
           [hiccup.page]))

(defn list-s3
  [request]
  (let [response (amazonica.aws.s3/list-objects request)
        next-request (assoc request :marker (:next-marker response))]
    (concat (:object-summaries response) (if (:truncated? response)
                                           (lazy-seq (list-s3 next-request))
                                           []))))

(defn page
  [keys]
  (let [li (fn [key]
             (let [href (str "https://s3.amazonaws.com/" :bucket "/" key)]
               [:li [:a {:href href} key]]))]
    (hiccup.page/html5 [:body [:ul (map li keys)]])))

(defn -main
  [& args]
  (page (map :key (take 5 (list-s3 {:bucket-name "testing"})))))
