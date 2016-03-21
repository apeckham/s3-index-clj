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

(def object-url #(str "https://s3.amazonaws.com/" (:bucket-name %) "/" (:key %)))

(defn page
  [objects]
  (let [li (fn [object]
             [:li [:img.img-responsive {:src (object-url object)}]])]
    (hiccup.page/html5 [:head
                        (hiccup.page/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
                        (hiccup.page/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css")
                        (hiccup.page/include-js "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js")
                        [:style "body { background-color: lightgrey }"]]
                       [:body
                        [:ul.list-unstyled (map li objects)]])))

(defn -main
  [& args]
  (doseq [[index partition] (->> (list-all-objects {:bucket-name "***REMOVED***"})
                                 #_(sort-by :last-modified)
                                 (partition-all 50)
                                 (take 1)
                                 (map-indexed vector))]
    (spit (str "/tmp/page" index ".html") (page partition))))
