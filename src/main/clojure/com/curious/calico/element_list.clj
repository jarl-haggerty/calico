(ns com.curious.calico.element-list
  (:import javax.swing.JPanel
           javax.swing.JCheckBox
           javax.swing.JList
           javax.swing.JScrollPane
           javax.swing.JButton
           javax.swing.JOptionPane
           javax.swing.DefaultListModel
           javax.swing.AbstractAction
           javax.swing.BoxLayout
           java.awt.Component
           java.awt.event.MouseAdapter
           javax.swing.ListSelectionModel)
  (:use com.curious.calico.calico-element
        com.curious.calico.calico-state
        com.curious.calico.element-editor))

(def element-panel (JPanel.))

(def list-model (DefaultListModel.))

(def element-list (JList. list-model))

(def button-panel (JPanel.))

(def show-invisible (JCheckBox.))

(defn update-list []
  (.clear list-model)
  (when (seq @projects)
    (doseq [element (@projects (.getSelectedIndex project-pane))
            :when (or (.isSelected show-invisible) (:display element))]
      (.addElement list-model element))
    (.repaint (:panel (@projects (.getSelectedIndex project-pane))))))

(.setAction show-invisible (proxy [AbstractAction] ["Show Invisible Elements"]
                           (actionPerformed [_]
                                            (update-list))))

(doto element-list
  (.setSelectionMode ListSelectionModel/MULTIPLE_INTERVAL_SELECTION)
  (.addMouseListener (proxy [MouseAdapter] []
                       (mouseClicked [e]
                                     (when (= (.getClickCount e) 2)
                                       (edit-element (.getSelectedValue element-list))
                                       (update-list))))))

(def add-button (JButton. (proxy [AbstractAction] ["Add"]
                            (actionPerformed [_]
                                             (swap! (:elements (@projects (.getSelectedIndex project-pane)))
                                                    #(conj % (assoc default-element :role "Obstacle" :calico-display "None")))))))

(def remove-button (JButton. (proxy [AbstractAction] ["Remove"]
                               (actionPerformed [_]
                                                (let [to-remove (.getSelectedValues element-list)]
                                                  (swap! (:elements (@projects (.getSelectedIndex project-pane)))
                                                         (fn [x] (filter #(not (.contains to-remove %)) x))))))))

(def edit-button (JButton. (proxy [AbstractAction] ["Edit"]
                             (actionPerformed [_]
                                              (let [elements (.getSelectedValues element-list)]
                                                (if (= (.size elements) 1)
                                                  (edit-element (.get elements 0))
                                                  (JOptionPane/showMessageDialog frame
                                                                                 "Can only edit one element at a time."
                                                                                 "Multiple Edit Error"
                                                                                 JOptionPane/ERROR_MESSAGE)))))))

(doto element-panel
  (.setLayout (BoxLayout. element-panel BoxLayout/PAGE_AXIS))
  (.add (doto (JScrollPane. element-list)
          (.setAlignmentX Component/LEFT_ALIGNMENT)))
  (.add (doto show-invisible
          (.setAlignmentX Component/LEFT_ALIGNMENT)))
  (.add (doto button-panel
          (.setLayout (BoxLayout. button-panel BoxLayout/LINE_AXIS))
          (.add add-button)
          (.add remove-button)
          (.add edit-button)
          (.setAlignmentX Component/LEFT_ALIGNMENT))))

(.add tools-pane element-panel)
