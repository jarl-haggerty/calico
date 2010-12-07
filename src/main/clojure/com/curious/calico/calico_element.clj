(ns com.curious.calico.calico-element)

(def default-element {:name ""
                      :x 0 :y 0
                      :width 0 :height 0
                      :view-x 0 :view-y 0
                      :image-left 0 :image-right 1 :image-bottom 0 :image-top 1
                      :attach-to-view false})

(defn from-xml [element-xml]
  (let [raw-map (apply into {} [:tag (first element-xml)] (map #(vector (:tag %) (first (:content %))) (:content element-xml)))]
    (assoc raw-map
      :x (Double/valueOf (get raw-map :x))
      :y (Double/valueOf (get raw-map :y))
      :width (Double/valueOf (get raw-map :width))
      :height (Double/valueOf (get raw-map :height))
      :view-x (Double/valueOf (get raw-map :view-x))
      :view-y (Double/valueOf (get raw-map :view-y))
      :image-left (Double/valueOf (get raw-map :image-left))
      :image-right (Double/valueOf (get raw-map :image-right))
      :image-bottom (Double/valueOf (get raw-map :image-bottom))
      :image-top (Double/valueOf (get raw-map :image-top))
      :attach-to-view (Boolean/valueOf (get raw-map :attach-to-view)))))

(defn to-xml [element]
  (apply vector (keyword (:name element)) (for [[k, v] (dissoc :name)]
                                            [k (str v)])))

(defn render [g offset-x offset-y]
  )
