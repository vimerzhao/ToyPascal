package wci.ide.commons;

import wci.ide.EditPane;

import javax.swing.*;
import java.io.File;

public class EditFile {
    private File file;
    private boolean saved;
    private JInternalFrame iframe;
    private EditPane editPane;
    public EditFile(File file, boolean saved, JInternalFrame iframe, EditPane editPane) {
        this.file = file;
        this.saved = saved;
        this.iframe = iframe;
        this.editPane = editPane;
    }

    public EditPane getEditPane() {
        return editPane;
    }

    public void setEditPane(EditPane editPane) {
        this.editPane = editPane;
    }

    public JInternalFrame getIframe() {
        return iframe;
    }

    public void setIframe(JInternalFrame iframe) {
        this.iframe = iframe;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
