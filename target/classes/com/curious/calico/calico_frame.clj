(ns com.curious.calico.calico-frame
  (:import javax.swing.JFrame
           javax.swing.JToolBar
           javax.swing.JMenuBar
           javax.swing.JMenu
           javax.swing.AbstractAction
           javax.swing.JFileChooser
           javax.swing.JTabbedPane
           javax.swing.ImageIcon
           javax.imageio.ImageIO
           javax.swing.JComponent
           javax.swing.JOptionPane
           java.awt.GridBagLayout
           java.awt.GridBagConstraints
           java.awt.Insets)
  (:use clojure.java.io
        com.curious.calico.calico-project
        com.curious.calico.texture-tool
        com.curious.calico.element-list))

(def default-element {:name ""
                      :x 0 :y 0
                      :width 0 :height 0
                      :view-x 0 :view-y 0
                      :image-left 0 :image-right 1 :image-bottom 0 :image-top 1
                      :attach-to-view false})

(defn add-scenery-action [frame]
  (proxy [AbstractAction] ["Add Scenery"
                           (ImageIcon. (ClassLoader/getSystemResource "icons/scenery.png"))]
    (actionPerformed [_]
                     (let [file-chooser (JFileChooser. (file "resources"))]
                       (if (= (.showOpenDialog (file-chooser))
                              JFileChooser/APPROVE_OPTION)
                         (let [chosen (.getSelectedFile file-chooser)]
                           (copy chosen (file (str "resources/" (.getName chosen))))
                           (swap! (:new-element frame)
                                  (fn [x] (merge default-element {:role "Scenery"
                                                                 :calico-display (.getName chosen)})))))))))
(defn add-ll-action [frame]
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! (:new-element frame)
                                     (fn [x] (merge default-element {:role "Obstacle"
                                                                    :calico-display "LL"})))))))))
(defn add-lr-action [frame]
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! (:new-element frame)
                                     (fn [x] (merge default-element {:role "Obstacle"
                                                                    :calico-display "LL"})))))))))
(defn add-ur-action [frame]
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! (:new-element frame)
                                     (fn [x] (merge default-element {:role "Obstacle"
                                                                    :calico-display "LL"})))))))))
(defn add-ul-action [frame]
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! (:new-element frame)
                                     (fn [x] (merge default-element {:role "Obstacle"
                                                                    :calico-display "LL"})))))))))
(defn add-rectangle-action [frame]
     (proxy [AbstractAction] ["Add Rectangle"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/rectangle.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! (:new-element frame)
                                     (fn [x] (merge default-element {:role "Obstacle"
                                                                    :calico-display "Rectangle"})))))))))
(defn add-ellipse-action [frame]
     (proxy [AbstractAction] ["Add Ellipse"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/ellipse.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! (:new-element frame)
                                     (fn [x] (merge default-element {:role "Obstacle"
                                                                    :calico-display "Ellipse"})))))))))
(defn new-action [frame]
  (proxy [AbstractAction] ["New"]
    (actionPerformed [_]
                     (let [new-project (calico-project)]
                       (swap! (:projects frame) #(conj % new-project))
                       (.addTab (:tabs frame) "" (:panel new-project))))))
(defn close-action [frame]
  (proxy [AbstractAction] ["Close"]
    (actionPerformed [_]
                     (let [selected-panel (.getSelectedIndex (:tabs frame))]
                       (.remove (:tabs frame) selected-panel)
                       (swap! (:projects frame)
                              (fn [x] (concat (subvec x 0 selected-panel)
                                             (subvec x (inc selected-panel)))))))))
(defn open-action [frame]
  (proxy [AbstractAction] ["Open"]
    (actionPerformed [_]
                     (let [file-chooser (JFileChooser.)]
                       (if (= (.showOpenDialog file-chooser)
                              JFileChooser/APPROVE_OPTION)
                         (if-let [new-project (calico-project (.getSelectedFile file-chooser))]
                           (.addTab (:tabs frame) (:title new-project) (:panel new-project))
                           (JOptionPane/showMessageDialog (:frame frame)
                                                          "Failed to load file."
                                                          "Error"
                                                          JOptionPane/ERROR_MESSAGE)))))))
(defn save-action [frame]
  (proxy [AbstractAction] ["Save"]
    (actionPerformed [_]
                     (let [selected-panel (.getSelectedIndex (:tabs frame))]
                       (save ((:projects frame) selected-panel))
                       (.setTitleAt (:tabs frame) selected-panel (:title ((:projects frame) selected-panel)))))))
(defn save-as-action [frame]
  (proxy [AbstractAction] ["Save"]
    (actionPerformed [_]
                     (let [selected-panel (.getSelectedIndex (:tabs frame))]
                       (save-as ((:projects frame) selected-panel))
                       (.setTitleAt (:tabs frame) selected-panel (:title ((:projects frame) selected-panel)))))))
(defn quit-action [frame]
  (proxy [AbstractAction] ["Quit"]
    (actionPerformed [_]
                     (System/exit 0))))
(defn calico-frame []
  (let [icon-image (ImageIO/read (ClassLoader/getSystemResource "icons/cat.png"))
        jmenubar (JMenuBar.)
        jtoolbar (JToolBar.)
        program {:frame (doto (JFrame. "Calico Editor 3.0")
                          (.setIconImage icon-image)
                          (.setMenuBar jmenubar))
                 :project-pane (JTabbedPane.)
                 :tools-pane (JTabbedPane.)
                 :projects (atom [])
                 :tools {:element-list (element-list) :texture-tool (texture-tool)}
                 :new-element (atom nil)}]
    (doto jmenubar
      (.add (doto (JMenu. "File")
              (.add (new-action program))
              (.add (close-action program))
              (.add (save-action program))
              (.add (save-as-action program))
              (.add (quit-action program)))))
    (doto jtoolbar
      (.add (add-scenery-action program))
      (.add (add-ll-action program))
      (.add (add-lr-action program))
      (.add (add-ur-action program))
      (.add (add-ul-action program))
      (.add (add-rectangle-action program))
      (.add (add-ellipse-action program))
      (.setAlignmentX JComponent/LEFT_ALIGNMENT))
    (if (.exists (file "resources"))
      (.mkdir (file "resources")))
    (doto (.getContentPane (:frame program))
      (.setLayout (GridBagLayout.))
      (.add jtoolbar (GridBagConstraints. 0 0
                                         1 1
                                         1 0
                                         GridBagConstraints/CENTER
                                         GridBagConstraints/HORIZONTAL
                                         (Insets. 0 0 0 0)
                                         0 0))
      (.add (:project-pane program) (GridBagConstraints. 0 1
                                        1 1
                                        1 1
                                        GridBagConstraints/CENTER
                                        GridBagConstraints/BOTH
                                        (Insets. 0 0 0 0)
                                        0 0))
      (.add (:tools-pane program) (GridBagConstraints. 1 1
                                       1 1
                                       0 1
                                       GridBagConstraints/CENTER
                                       GridBagConstraints/BOTH
                                       (Insets. 0 0 0 0)
                                       0 0)))))

