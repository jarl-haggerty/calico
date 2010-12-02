(ns com.curious.calico.core
  (:import java.awt.Toolkit)
  (:use com.curious.calico.calico-frame)
  (:gen-class))

(defn -main [& args]
  (let [dim (-> (Toolkit/getDefaultToolkit) .getScreenSize)
        width (.getWidth dim)
        height (.getHeight dim)]
    (doto (:frame (calico-frame))
      (.setLocation (/ width 8) (/ height 8))
      (.setSize (* width 3/4) (* height 3/4))
      (.setVisible true))))

