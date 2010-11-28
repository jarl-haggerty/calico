(ns calico.core
  (:import java.awt.Toolkit)
  (:use calico.calico-frame))

(let [dim (-> (Toolkit/getDefaultToolkit) .getScreenSize)
      width (.getWidth dim)
      height (.getHeight dim)]
  (doto (calico-frame)
    (set-location (/ width 8) (/ height 8))
    (set-size (* width 3/4) (* height 3/4))
    (set-visible true)))

