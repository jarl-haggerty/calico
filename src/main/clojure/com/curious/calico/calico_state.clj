(ns com.curious.calico.calico-state
  (:import javax.swing.JFrame
           javax.swing.JTabbedPane))

(def new-element (atom nil))
(def projects (atom []))
(def frame (JFrame. "Calico"))
(def project-pane (JTabbedPane.))
(def tools-pane (JTabbedPane.))
(def running (atom true))
