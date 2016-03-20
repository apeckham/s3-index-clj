(ns s3-index.core
  (:gen-class)
  (require [amazonica.aws.s3]
           [hiccup.page]))

(defn list-all-objects
  [request]
  (let [response (amazonica.aws.s3/list-objects request)
        next-request (assoc request :marker (:next-marker response))]
    (concat (:object-summaries response) (if (:truncated? response)
                                           (lazy-seq (list-all-objects next-request))
                                           []))))

(defn page
  [objects]
  (let [li (fn [object]
             (let [src (str "https://s3.amazonaws.com/" (:bucket-name object) "/" (:key object))]
               [:li [:img {:src src}]]))]
    (hiccup.page/html5 [:body [:ul (map li objects)]])))

(defn -main
  [& args]
  (let [objects (list-all-objects {:bucket-name "***REMOVED***"})]
    (doseq [[index partition] (map-indexed vector (partition-all 50 objects))]
      (spit (str "/tmp/page" index ".html") (page partition)))))
