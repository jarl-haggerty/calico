(ns com.curious.calico.element-list
  (:use com.curious.calico.calico-element))

(def element-panel (JPanel.))

(def list-model (DefaultListModel.))

(def element-list (JList. list-model))

(def show-invisible)

(defn update-list []
  (.clear list-model)
  (when (seq @projects)
    (doseq [element (@projects (.getSelectedIndex project-pane))
            :when (or (.isSelected show-invisble) (:display element))]
      (.addElement list-model element))
    (.repaint (:panel (@projects (.getSelectedIndex project-pane))))))

(def show-invisible (JCheckBox. (proxy [AbstractAction] ["Show Invisible Elements"]
                                  (actionPerformed [_]
                                                   (update-list)))))

(doto element-list
  (.setSelectionMode ListSelectionModel/MULTIPLE_INTERVAL_SELECTION)
  (.addMouseListener (proxy [MouseAdapter] []
                       (mouseClicked [e]
                                     (when (= (.getClickCount e) 2)
                                       (edit-element (.getSelectedValue element-list))
                                       (update-list)))))))

(def add-button (JButton. (proxy [AbstractAction] ["Add"]
                            (actionPerformed [_]
                                             (swap! (:elements (@projects (.getSelectedIndex project-pane)))
                                                    #(conj % (assoc default-element :role "Obstacle" :calico-display "None")))))))
(def remove-button (JButton. (proxy [AbstractAction] ["Remove"]
                               (actionPerformed [_]
                                                ))))
(def edit-button (JButton. (proxy [AbstractAction] ["Edit"]
                             (actionPerformed [_]
                                              ))))

(doto element-panel
  (.setLayout (BoxLayout. element-panel BoxLayout/PAGE_AXIS))
  (.add (doto (JScrollPane. element-list)
          (.setAlignmentX Component.LEFT_ALIGNMENT)))
  (.add (doto show-invisble
          (.setAlignmentX Component.LEFT_ALIGNMENT)))
  (.add (do (JPanel.)
            (.setLayout (BoxLayout. element-panel BoxLayout/LINE_AXIS))
            (.add add-button)
            (.add remove-button)
            (.add edit-button))))
