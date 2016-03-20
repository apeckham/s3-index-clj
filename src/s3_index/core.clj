(ns s3-index.core
  (:gen-class))

(defn list-s3
  [request]
  (let [response (amazonica.aws.s3/list-objects request)
        next-request (assoc request :marker (:next-marker response))]
    (concat (:object-summaries response) (if (:truncated? response)
                                           (lazy-seq (list-s3 next-request))
                                           []))))

(defn -main
  [& args]
  (prn 5))
