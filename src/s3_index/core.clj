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

(defn li
  [object]
  (let [url (object-url (str "https://s3.amazonaws.com/" (:bucket-name object) "/" (:key object)))]
    [:li.img-li
     [:img.img-responsive {:src url}]
     [:div.text-center
      (:last-modified object)]
     [:div.text-center
      [:a {:href url} url]]]))

(defn page
  [index objects]
  (let [nav [:nav
             [:ul.pager
              (if-not (zero? index) [:li [:a {:href (str "page" (dec index) ".html")} "Previous"]])
              [:li [:a {:href (str "page" (inc index) ".html")} "Next"]]]]]
    (hiccup.page/html5 [:head
                        (hiccup.page/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
                        (hiccup.page/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css")
                        [:style "body { background-color: lightgrey }
                               .img-li { border-bottom: 1px solid black; margin-bottom: 100px }"]]
                       [:body
                        nav
                        [:ul.list-unstyled (map li objects)]
                        nav])))

(defn -main
  [& args]
  (doseq [[index objects] (->> (list-all-objects {:bucket-name (first args)})
                               (sort-by :last-modified)
                               (partition-all 50)
                               (map-indexed vector))]
    (spit (str "/tmp/page" index ".html") (page index objects))))
