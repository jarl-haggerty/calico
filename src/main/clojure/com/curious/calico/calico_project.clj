(ns com.curious.calico.calico-project
  (:import javax.swing.JFileChooser
           javax.swing.JPanel
           javax.swing.JScrollBar
           java.awt.event.AdjustmentListener)
  (:use clojure.java.io)
  (:require [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.contrib.zip-filter.xml :as zf]))

(def mouse-pressed (atom false))
(def mouse-pressed-position (atom [0 0]))

(def horizontal-scroll-bar (doto (JScrollBar. JScrollBar/HORIZONTAL)
                             (.addAdjustmentListener scroll-listener)))

(def vertical-scroll-bar (doto (JScrollBar. JScrollBar/VERTICAL)
                           (.addAdjustmentListener scroll-listener)))

(def project-panel (proxy [JPanel] []
                    (paintComponent [g]
                                    (let [project (nth projects (.getSelectedIndex project-pane))
                                          background-color @(:background-color project)
                                          foreground-color (Color. (- 255 (.getRed background-color))
                                                                   (- 255 (.getGreen background-color))
                                                                   (- 255 (.getBlue background-color)))
                                          grid-color (Color. (+ (/ (- (.getRed background-color)  (.getRed foregroundColor)) 2)
                                                                (Math/min (.getRed foreground-color) (.getRed background-color)))
                                                             (+ (/ (- (.getGreen background-color)  (.getGreen foregroundColor)) 2)
                                                                (Math/min (.getGreen foreground-color) (.getGreen background-color)))
                                                             (+ (/ (- (.getBlue background-color)  (.getBlue foregroundColor)) 2)
                                                                (Math/min (.getBlue foreground-color) (.getBlue background-color))))
                                          select-color (Color. (* 255 (Math/round (/ (.getRed background-color) 255.0)))
                                                               (.getGreen foreground-color)
                                                               (.getBlue foreground-color))]
                                      (.getColor g background-color)
                                      (.fillRect g 0 0 @(:width project) @(:height project))
                                      (when @(:use-grid)
                                        (.setColor g grid-color)
                                        (let [offset-x (mod @(:x project) @(:grid-spacing project))
                                              offset-y (mod @(:y project) @(:grid-spacing project))]
                                          (doseq [a (range (- @(:grid-spacing project) offset-x) (.getWidth this) @(:grid-spacing))]
                                            (.drawLine g a 0 a (.getHeight this)))
                                          (doseq [a (range (- @(:grid-spacing project) offset-y) (.getHeight this) @(:grid-spacing))]
                                            (.drawLine g 0 a (.getWidth this) a))))
                                      (doseq [element @(:elements project)]
                                        (render element g @(:x project) @(:y project)))
                                      (when @mouse-pressed
                                        (.setColor g foreground-color)
                                        (.drawRect g
                                                   (Math/min (@mouse-pressed-position 0) (@mouse-position 0)) (Math/min (@mouse-pressed-position 1) (@mouse-position 1))
                                                   (Math/abs (- (@mouse-pressed-position 0) (@mouse-position 0))) (Math/abs (- (@mouse-pressed-position 1) (@mouse-position 1)))))
                                      (.setColor g Color/cyan)
                                      (if (< (- @(:width project) @(:x project)) (.getWidth this))
                                        (.fillRect g (- @(:width project) @(:x project)) 0 (.getWidth this) (.getHeight this)))
                                      (if (< (- @(:height project) @(:y project)) (.getHeight this))
                                        (.fillRect g 0 @(:height project) (.getWidth this) (.getHeight this)))))))

(doto project-panel
  (.setLayout (BorderPanel.))
  (.add horizontal-scroll-bar BorderLayout/SOUTH)
  (.add vertical-scroll-bar BorderLayout/EAST))

(defn resize-world [project]
  (swap! (:width project) #(int (* % (:pixels-per-meter project))))
  (swap! (:height project) #(int (* % (:pixels-per-meter project))))
  (correct-scroll-bars project)
  (.repaint (:panel project)))

(defn resize-world [project width height]
  (swap! (:width project) (fn [x] width))
  (swap! (:height project) (fn [x] height))
  (swap! (:scaled-width project) (fn [x] (/ width @(:pixels-per-meter project))))
  (swap! (:scaled-height project) (fn [x] (/ width @(:pixels-per-meter project))))
  (correct-scroll-bars project)
  (.repaint (:panel project)))

(defn resize-scaled-world [project width height]
  (swap! (:scaled-width project) (fn [x] width))
  (swap! (:scaled-height project) (fn [x] height))
  (swap! (:width project) (fn [x] (int (* width @(:pixels-per-meter project)))))
  (swap! (:height project) (fn [x] (int (* width @(:pixels-per-meter project)))))
  (correct-scroll-bars project)
  (.repaint (:panel project)))

(def over-element-menu (JPopupMenu.))
(def over-nothing-menu (JPopupMenu.))
(def visible-menu (atom nil))

(def bring-to-front-action (proxy [AbstractAction] ["Bring To Front"]
                             (actionPerformed [_]
                                              (let [project (nth projects (.getSelectedIndex project-pane))])
                                              (if-let [selected (seq @(:selected project))]
                                                (swap! (:elements project)
                                                       (fn [elements]
                                                         (let [ordered-selected (filter #(contains? selected %)
                                                                                        elements)
                                                               ordered-unselected (filter #(not (contains? selected %))
                                                                                          elements)]
                                                           (reduce #(cons %1 %2) (reverse ordered-unselected) ordered-selected))))
                                                (if-let [selected (element-at project (.getX @visible-menu) (.getY @visible-menu))]
                                                  (swap! (:elements project)
                                                         (fn [elements]
                                                           (reverse (cons selected
                                                                          (reverse (filter #(not= selected %)
                                                                                           elements)))))))))))

(def send-to-back-action (proxy [AbstractAction] ["Send To Back"]
                           (actionPerformed [_]
                                            (let [project (nth projects (.getSelectedIndex project-pane))])
                                            (if-let [selected (seq @(:selected project))]
                                              (swap! (:elements project)
                                                     (fn [elements]
                                                       (let [ordered-selected (filter #(contains? selected %)
                                                                                      elements)
                                                             ordered-unselected (filter #(not (contains? selected %))
                                                                                        elements)]
                                                         (reduce #(cons %2 %1) ordered-unselected (reverse ordered-selected)))))
                                              (if-let [selected (element-at project (.getX @visible-menu) (.getY @visible-menu))]
                                                (swap! (:elements project)
                                                       (fn [elements]
                                                         (reverse (cons selected
                                                                        (filter #(not= selected %)
                                                                                elements))))))))))

(def resize-world-action (proxy [AbstractAction] ["Resize World"]
                           (actionPerformed [e]
                                            (let [project (nth projects (.getSelectedIndex project-pane))]
                                              (resize-world project (.getX @visible-menu) (.getY @visible-menu))))))

(def edit-element-action (proxy [AbstractAction] ["Edit Element"]
                           (actionPerformed [e]
                                            (let [project (nth projects (.getSelectedIndex project-pane))]
                                              (edit-element (element-at project (.getX @visible-menu) (.getY @visible-menu)))))))

(doto over-element-menu
  (.add bring-to-front-action)
  (.add send-to-back-action)
  (.add resize-world-action)
  (.add edit-element-action))

(doto over-nothing-menu
  (.add bring-to-front-action)
  (.add send-to-back-action)
  (.add resize-world-action))

(def mouse-listener (proxy [MouseAdapter] []
                      (mousePressed [e]
                                    (swap! mouse-pressed (fn [x] true))
                                    (swap! mouse-pressed-position (fn [x] [(.getX e) (.getY e)])))
                      (mouseReleased [e]
                                     (swap! mouse-pressed (fn [x] false))
                                     (let [project (nth projects (.getSelectedIndex project-pane))]
                                          (if (= (.getButton e) 1)
                                            (if (= (.getClickCount e) 1)
                                              (if @new-element
                                                (let [transformed-element (assoc @new-element
                                                                            :panel-x (+ (.getX e) @(:x panel))
                                                                            :panel-y (+ (.getY e) @(:y panel)))])
                                                (swap! new-element (fn [x] nil))
                                                (swap! (:elements project)
                                                       (fn [elements]
                                                         (reverse (cons transformed-element (reverse elements)))))
                                                (if-let [new-selected (element-at project (.getX e) (.getY e))]
                                                  ))
                                              (edit-element (element-at project (.getX e) (.getY e))))
                                            (swap! visible-menu (fn [x]
                                                                  (if (element-at project (.getX e) (.getY e))
                                                                    (.show over-element-menu project-panel (.getX e) (.getY e))
                                                                    (.show over-nothing-menu project-panel (.getX e) (.getY e)))))
                                            (.show @visible-menu project-panel (.getX e) (.getY e)))))))

(def mouse-motion-listener (proxy [MouseMotionAdapter] []
                             (mouseMoved [e]
                                         (swap! mouse-position (fn [x] [(.getX e) (.getY e)])))))

(defn save [project]
  (if @(:file project)
    (let [level-data [:level
                      [:pixels-per-meter (:pixels-per-meter project)]
                      [:width (:width project)]
                      [:height (:height project)]
                      [:use-grid (:use-grid project)]
                      [:background-color (:background-color project)]
                      [:grid-spacing (:grid-spacing project)]]
          element-data (map to-xml (:elements project))]
      (with-out-str (binding [ *prxml-indent* 2]
                      (prxml [:calico level-data element-data]))))
    (save-as project)))

(defn save-as [project]
  (let [file-chooser (JFileChooser.)]
    (.showSaveDialog)
    (when-let [selected-file (.getSelectedFile file-chooser)]
      (if (= (.lastIndexOf (.getName selected-file))
             (- (.length (.getName selected-file)) 4))
        (do (swap! (:file project) (fn [x] selected-file))
            (swap! (:title project) (fn [x] (.substring (.getName (:file project))
                                                       0
                                                       (- (.length (.getName (:file project))) 4)))))
        (do (swap! (:file project) (fn [x] (file (str (.getAbsolutePath x) ".xml"))))
            (swap! (:title project (fn [x] (.getName (:file project)))))))
      (save project))))

(defn open [project]
  (when-let [source-file @(:file project)]
          (swap! (:title project) (fn [x] (.replace (.getName source-file) ".xml" "")))
          (let [xml-contents (zip/xml-zip (xml/parse source-file))]
            (swap! (:pixels-per-meter project) (fn [x] (zf/xml-> xml-conents :level :pixels-per-meter zf/text)))
            (swap! (:background-color project) (fn [x] (let [pieces (.split (zf/xml-> xml-conents :level :background-color zf/text) ",")]
                                                        (apply #(Color. %1 %2 %3) (map #(Integer/valueOf %) pieces)))))
            (swap! (:use-grid project) (fn [x] (Boolean/valueOf (zf/xml-> xml-conents :level :use-grid zf/text))))
            (swap! (:grid-spacing project) (fn [x] (Double/valueOf (zf/xml-> xml-conents :level :grid-spacing zf/text))))
            (swap! (:width project) (fn [x] (Double/valueOf (zf/xml-> xml-conents :level :width zf/text))))
            (swap! (:height project) (fn [x] (Double/valueOf (zf/xml-> xml-conents :level :height zf/text))))
            (swap! (:elements project) (for [element (zf/xml-> xml-conents :elements children)]
                                         (from-xml (zip/node element)))))))

(defn- correct-scroll-bars [project]
  (.setValue (:horizontal-scroll-bar project) (-> (:x project)
                                                  (Math/min (- @(:width project)
                                                               (- (.getWidth (:panel project))
                                                                  (.getWidth (:vertical-scroll-bar project)))))
                                                  (Math/max 0)))
  (.setValue (:vertical-scroll-bar project) (-> (:y project)
                                                (Math/min (- @(:height project)
                                                             (- (.getHeight (:panel project))
                                                                (.getHeight (:horizontal-scroll-bar project)))))
                                                (Math/max 0)))
  (.setVisibleAmount (:horizontal-scroll-bar project) (- (.getWidth (:panel project))
                                                         (.getWidth (:vertical-scroll-bar project))))
  (.setVisibleAmount (:vertical-scroll-bar project) (- (.getHeight (:panel project))
                                                       (.getHeight (:horizontal-scroll-bar project)))))

(def scroll-listener (proxy [AdjustmentListener] []
                       (adjustmentValueChanged [e]
                                               (let [project (nth projects (.getSelectedIndex project-pane))
                                                     axis (if (-> e (.getSource) (.getOrientation) (= JScrollBar/HORIZONTAL))
                                                            (:x project)
                                                            (:y project))]
                                                 (swap! axis
                                                        (fn [x] (.getValue e)))))))

(defn calico-project
  ([] (calico-project nil))
  ([source-file]
     (let [project {:title (atom "")
                    :x (atom 0)
                    :y (atom 0)
                    :width (atom 0)
                    :height (atom 0)
                    :scaled-width (atom 0)
                    :scaled-height (atom 0)
                    :popup-
                    :elements (atom [])
                    :selected (atom [])
                    :clipboard (atom [])
                    :file (atom source-file)
                    :background-color (atom Color/white)
                    }]
        (.add project-pane project-panel)
        (resize-world new-project))))
