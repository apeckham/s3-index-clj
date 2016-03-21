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

(defn li
  [object]
  [:li [:img.img-responsive {:src (object-url object)}]])

(defn page
  [index objects]
  (hiccup.page/html5 [:head
                      (hiccup.page/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
                      (hiccup.page/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css")
                      (hiccup.page/include-js "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js")
                      [:style "body { background-color: lightgrey }"]]
                     [:body
                      [:nav
                       [:ul.pager
                        [:li [:a {:href (str "page" (dec index) ".html")} "Previous"]]
                        [:li [:a {:href (str "page" (inc index) ".html")} "Next"]]]]
                      [:ul.list-unstyled (map li objects)]]))

(defn -main
  [& args]
  (doseq [[index objects] (->> (list-all-objects {:bucket-name "***REMOVED***"})
                               #_(sort-by :last-modified)
                               (partition-all 50)
                               (take 1)
                               (map-indexed vector))]
    (spit (str "/tmp/page" index ".html") (page index objects))))
