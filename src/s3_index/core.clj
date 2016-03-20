(ns s3-index.core
  (:gen-class))

(defn list-s3
  [request]
  (let [response (amazonica.aws.s3/list-objects request)
        object-summaries (:object-summaries response)
        next-request (assoc request :marker (:next-marker response))]
    (if (:truncated? response)
      (concat object-summaries (lazy-seq (list-s3 next-request)))
      object-summaries)))

(defn -main
  [& args]
  (prn 5))
