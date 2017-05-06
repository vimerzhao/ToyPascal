package wci.ide;

import wci.ide.EditorFrame;
import wci.ide.commons.EditFile;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TabListener implements ChangeListener {
    private EditorFrame editorFrame;
    public TabListener(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        int index = tabbedPane.getSelectedIndex();

        if (index == -1) return;

        JInternalFrame currentFrame = editorFrame.getIFrame(tabbedPane.getToolTipTextAt(index));
        editorFrame.showIFrame(currentFrame);
        EditFile currentFile = editorFrame.getEditFile(currentFrame);
        editorFrame.setCurrentFile(currentFile);
    }
}
