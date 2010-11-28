(ns calico.calico-frame
  (:import java.swing.JFrame))

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
                              (swap! (:new-element frame) (fn [x] (merge default-element {:role "Scenery"
                                                                                          :calico-display (.getName chosen)})))))))))
(def add-ll-action
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (merge default-element {:role "Obstacle"
                                                      :calico-display "LL"})))))))

(def add-lr-action
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (merge default-element {:role "Obstacle"
                                                      :calico-display "LL"})))))))

(def add-ur-action
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (merge default-element {:role "Obstacle"
                                                      :calico-display "LL"})))))))

(def add-ul-action
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (merge default-element {:role "Obstacle"
                                                      :calico-display "LL"})))))))

(def add-rectangle-action
     (proxy [AbstractAction] ["Add Rectangle"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/rectangle.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (merge default-element {:role "Obstacle"
                                                      :calico-display "Rectangle"})))))))

(def add-ellipse-action
     (proxy [AbstractAction] ["Add Ellipse"
                              (ImageIcon. (ClassLoader/getSystemResource "icons/ellipse.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (merge default-element {:role "Obstacle"
                                                      :calico-display "Ellipse"})))))))

(defn new-action [frame]
  (proxy [AbstractAction] ["New"]
    (actionPerformed [_]
		     (let [new-project (calico-project frame)]
		       (swap! (:projects frame) #(conj % new-project))
                       (.addTab (:project-panels frame) "" (:panel new-project))))))
(defn close-action [panels]
  (proxy [AbstractAction] ["Close"]
    (actionPerformed [_]
                     (.remove panels
                              (.getSelectedComponent panels)))))
(defn open-action [frame panels]
  (proxy [AbstractAction] ["Open"]
    (actionPerformed [_]
                     (let [file-chooser (JFileChooser.)]
                       (if (= (.showOpenDialog file-chooser)
                              JFileChooser/APPROVE_OPTION)
                         (if-let [new-panel (calico-panel frame (.getSelectedFile) file-chooser)]
                           (.addTab panels (title new-panel) new-panel)
                           (JOptionPane/showMessageDialog frame
                                                          "Failed to load file."
                                                          "Error"
                                                          JOptionPane/ERROR_MESSAGE)))))))

(defn save-action [panels]
  (proxy [AbstractAction] ["Save"]
    (actionPerformed [_]
                     (save (.getSelectedComponent panels))
                     (.setTitleAt (.getSelectedIndex panels) (.getSelectedComponent panels)))))

(defn save-as-action [panels]
  (proxy [AbstractAction] ["Save"]
    (actionPerformed [_]
                     (save-as (.getSelectedComponent panels))
                     (.setTitleAt (.getSelectedIndex panels) (.getSelectedComponent panels)))))

(def quit-action
     proxy [AbstractAction] ["Quit"]
     (actionPerformed [_]
                      (System/exit 0)))

(defprotocol calico-frame-protocol
  )

(defn calico-frame
  (let [icon-image (ImageIO/read (ClassLoader/getSystemResource "icons/cat.png"))
        frame (JFrame. "Calico Editor 3.0")
        toolbar (doto (Toolbar.)
                  (.add add-scenery-action)
                  (.add add-ll-action)
                  (.add add-lr-action)
                  (.add add-ur-action)
                  (.add add-ul-action)
                  (.add add-rectangle-action)
                  (.add add-ellipse-action)
                  (.setAlignmentX JComponent/LEFT_ALIGNMENT))
        panels (doto (JTabbedPane.))
        tools (doto (JTabbedPane.)
                (.addTab element-list))
        element-list (calico-list)
        file-menu (doto (JMenu. "File")
                    (.add ))
        menubar (doto (JMenuBar.))]
    (if (.exists (file "resources"))
      (.mkdir (file "resources")))
    (doto (.getContentPane frame)
      (.setLayout (GridBagLayout.))
      (.add toolbar (GridBagConstraints. 0 0
                                         1 1
                                         1 0
                                         GridBagConstraints/CENTER
                                         GridBagConstraints/HORIZONTAL
                                         (Insets. 0 0 0 0)
                                         0 0))
      (.add panels (GridBagConstraints. 0 1
                                        1 1
                                        1 1
                                        GridBagConstraints/CENTER
                                        GridBagConstraints/BOTH
                                        (Insets. 0 0 0 0)
                                        0 0))
      (.add tools (GridBagConstraints. 1 1
                                       1 1
                                       0 1
                                       GridBagConstraints/CENTER
                                       GridBagConstraints/BOTH
                                       (Insets. 0 0 0 0)
                                       0 0))))
    {:new-element (atom nil)}))

(def new-element [frame]
     (let [result @(:new-element frame)]
       (if result
         (swap (:new-element frame) (fn [x] nil)))
       result))
