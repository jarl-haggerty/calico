(ns com.curious.calico.calico-frame
  (:import javax.swing.JToolBar
           javax.swing.JMenuBar
           javax.swing.JMenu
           javax.swing.AbstractAction
           javax.swing.JFileChooser
           javax.swing.ImageIcon
           javax.imageio.ImageIO
           javax.swing.JComponent
           javax.swing.JOptionPane
           java.awt.GridBagLayout
           java.awt.GridBagConstraints
           java.awt.Insets
           java.awt.event.WindowAdapter)
  (:use clojure.java.io
        com.curious.calico.calico-project
        com.curious.calico.calico-state
        com.curious.calico.calico-element))

(def add-scenery-action
     (proxy [AbstractAction] ["Add Scenery"
                              (ImageIcon. (ClassLoader/getSystemResource "com/curious/calico/icons/scenery.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! new-element
                                     (fn [x] (merge default-element {:role "Scenery"
                                                                    :calico-display (.getName chosen)})))))))))
(def add-ll-action 
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "com/curious/calico/icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! new-element
                                     (fn [x] (merge default-element {:role "Obstacle"
                                                                    :calico-display "LL"})))))))))
(def add-lr-action 
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "com/curious/calico/icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! new-element
                                     (fn [x] (merge default-element {:role "Obstacle"
                                                                    :calico-display "LL"})))))))))
(def add-ur-action 
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "com/curious/calico/icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! new-element
                                     (fn [x] (merge default-element {:role "Obstacle"
                                                                    :calico-display "LL"})))))))))
(def add-ul-action 
     (proxy [AbstractAction] ["Add LL"
                              (ImageIcon. (ClassLoader/getSystemResource "com/curious/calico/icons/ll.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! new-element
                                     (fn [x] (merge default-element {:role "Obstacle"
                                                                    :calico-display "LL"})))))))))
(def add-rectangle-action
  (proxy [AbstractAction] ["Add Rectangle"
                           (ImageIcon. (ClassLoader/getSystemResource "com/curious/calico/icons/rectangle.png"))]
    (actionPerformed [_]
                     (let [file-chooser (JFileChooser. (file "resources"))]
                       (if (= (.showOpenDialog (file-chooser))
                              JFileChooser/APPROVE_OPTION)
                         (let [chosen (.getSelectedFile file-chooser)]
                           (copy chosen (file (str "resources/" (.getName chosen))))
                           (swap! new-element
                                  (fn [x] (merge default-element {:role "Obstacle"
                                                                 :calico-display "Rectangle"})))))))))
(def add-ellipse-action 
     (proxy [AbstractAction] ["Add Ellipse"
                              (ImageIcon. (ClassLoader/getSystemResource "com/curious/calico/icons/ellipse.png"))]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser. (file "resources"))]
                          (if (= (.showOpenDialog (file-chooser))
                                 JFileChooser/APPROVE_OPTION)
                            (let [chosen (.getSelectedFile file-chooser)]
                              (copy chosen (file (str "resources/" (.getName chosen))))
                              (swap! new-element
                                     (fn [x] (merge default-element {:role "Obstacle"
                                                                    :calico-display "Ellipse"})))))))))
(def new-action 
     (proxy [AbstractAction] ["New"]
       (actionPerformed [_]
                        (let [new-project (calico-project)]
                          (swap! projects #(conj % new-project))))))
(def close-action 
     (proxy [AbstractAction] ["Close"]
       (actionPerformed [_]
                        (let [selected-panel (.getSelectedIndex project-pane)]
                          (swap! projects
                                 (fn [x] (concat (subvec x 0 selected-panel)
                                                (subvec x (inc selected-panel)))))))))
(def open-action 
     (proxy [AbstractAction] ["Open"]
       (actionPerformed [_]
                        (let [file-chooser (JFileChooser.)]
                          (if (= (.showOpenDialog file-chooser)
                                 JFileChooser/APPROVE_OPTION)
                            (if-let [new-project (calico-project (.getSelectedFile file-chooser))]
                              (JOptionPane/showMessageDialog frame
                                                             "Failed to load file."
                                                             "Error"
                                                             JOptionPane/ERROR_MESSAGE)))))))
(def save-action 
     (proxy [AbstractAction] ["Save"]
       (actionPerformed [_]
                        (let [selected-panel (.getSelectedIndex project-pane)]
                          (save (projects selected-panel))
                          (.setTitleAt project-pane selected-panel (:title (projects selected-panel)))))))
(def save-as-action 
     (proxy [AbstractAction] ["Save"]
       (actionPerformed [_]
                        (let [selected-panel (.getSelectedIndex project-pane)]
                          (save-as (projects selected-panel))
                          (.setTitleAt project-pane selected-panel (:title (projects selected-panel)))))))
(def quit-action 
     (proxy [AbstractAction] ["Quit"]
       (actionPerformed [_]
                        (System/exit 0))))
(def toolbar
     (doto (JToolBar.)
       (.add add-rectangle-action)
       (.add add-ellipse-action)
       (.add add-scenery-action)
       (.add add-ll-action)
       (.add add-lr-action)
       (.add add-ur-action)
       (.add add-ul-action)))

(doto frame
  (.setIconImage (ImageIO/read (ClassLoader/getSystemResource "com/curious/calico/icons/cat.png")))
  (.setJMenuBar (doto (JMenuBar.)
                  (.add (doto (JMenu. "File")
                          (.add new-action)
                          (.add close-action)
                          (.add save-action)
                          (.add save-as-action)
                          (.add quit-action)))))
  (.addWindowListener (proxy [WindowAdapter] []
                        (windowClosing [_]
                                       (System/exit 0)))))
(doto (.getContentPane frame)
      (.setLayout (GridBagLayout.))
      (.add toolbar (GridBagConstraints. 0 0
                                          1 1
                                          1 0
                                          GridBagConstraints/CENTER
                                          GridBagConstraints/HORIZONTAL
                                          (Insets. 0 0 0 0)
                                          0 0))
      (.add project-pane (GridBagConstraints. 0 1
                                              1 1
                                              1 1
                                              GridBagConstraints/CENTER
                                              GridBagConstraints/BOTH
                                              (Insets. 0 0 0 0)
                                              0 0))
      (.add tools-pane (GridBagConstraints. 1 1
                                            1 1
                                            0 1
                                            GridBagConstraints/CENTER
                                            GridBagConstraints/BOTH
                                            (Insets. 0 0 0 0)
                                            0 0)))
