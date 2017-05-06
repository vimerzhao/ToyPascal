package wci.ide;

import wci.ide.commons.EditFile;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class IFrameListener extends InternalFrameAdapter {
    private EditorFrame editorFrame;
    public IFrameListener(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        JInternalFrame iframe = editorFrame.getDesk().getSelectedFrame();
        int tabIndex = editorFrame.getTabIndex(iframe.getTitle());
        editorFrame.getTabbedPane().setSelectedIndex(tabIndex);
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        JInternalFrame iframe = (JInternalFrame) e.getSource();
        EditFile editFile = editorFrame.getCurrentFile();
        editorFrame.askSave(editFile);
        editorFrame.closeIFrame(iframe);
    }
}
