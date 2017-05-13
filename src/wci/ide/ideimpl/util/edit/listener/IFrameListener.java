package wci.ide.ideimpl.util.edit.listener;

import wci.ide.ideimpl.EditPane;
import wci.ide.ideimpl.util.edit.EditFile;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class IFrameListener extends InternalFrameAdapter {
    private EditPane editPane;

    public IFrameListener(EditPane editPane) {
        this.editPane = editPane;
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        JInternalFrame iframe = editPane.getDesktop().getSelectedFrame();
        int tabIndex = editPane.getTabIndex(iframe.getTitle());
        editPane.getTabPane().setSelectedIndex(tabIndex);
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        JInternalFrame iframe = (JInternalFrame) e.getSource();
        EditFile editFile = editPane.getCurrentFile();
        editPane.askSave(editFile);
        editPane.closeIFrame(iframe);
    }
}
