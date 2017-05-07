package wci.ide.ideimpl;

import wci.ide.ideimpl.util.edit.*;
import wci.ide.ideimpl.util.edit.listener.EditDocumentListener;
import wci.ide.ideimpl.util.edit.listener.IFrameListener;
import wci.ide.ideimpl.util.edit.listener.TabListener;
import wci.ide.ideimpl.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditPane extends Box{
    private JTabbedPane tabPane;
    private JDesktopPane desktop;
    private EditFile currentFile;
    private IFrameListener iframeListener;

    private List<EditFile> editFiles = new ArrayList<>();
    public EditPane(int axis) {
        super(axis);
        tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabPane.addChangeListener(new TabListener(this));
        desktop = new JDesktopPane();
        iframeListener = new IFrameListener(this);
        desktop.setBackground(Color.GRAY);
        add(tabPane);
        add(desktop);
    }

    public void openFile(File file) {
        if (currentFile != null) {
            if (file.equals(currentFile.getFile())) {
                return;
            }
        }
        EditFile openedFile = getOpenFile(file);
        if (openedFile != null) {
            openExistFile(openedFile, file);
            return;
        }
        openNewFile(file);
    }
    public void openExistFile(EditFile openedFile, File willOpenFile) {
        tabPane.setSelectedIndex(getFileIndex(willOpenFile));
        showIFrame(openedFile.getIframe());
        this.currentFile = openedFile;
        editFiles.add(openedFile);

    }
    public void showIFrame(JInternalFrame iframe) {
        try {
            iframe.setSelected(true);
            iframe.toFront();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentFile(EditFile currentFile) {
        this.currentFile = currentFile;
    }
    public JInternalFrame getIFrame(String title) {
        JInternalFrame[] iframes = desktop.getAllFrames();
        for (JInternalFrame iframe : iframes) {
            if (iframe.getTitle().equals(title)) return iframe;
        }
        return null;
    }


    private EditFile getEditFile(File file) {
        for (EditFile openFile : editFiles) {
            if (openFile.getFile().equals(file)) return openFile;
        }
        return null;
    }


    private int getFileIndex(File file) {
        EditFile openFile = getEditFile(file);
        if (openFile == null) {
            return -1;
        }
        return getTabIndex(openFile.getIframe().getToolTipText());
    }



    private EditFile getOpenFile(File file) {
        for (EditFile openFile : editFiles) {
            if (openFile.getFile().equals(file)) {
                return openFile;
            }
        }
        return null;
    }

    public void openNewFile(File file) {
        JInternalFrame iframe= new JInternalFrame(file.getAbsolutePath(), true, true, true, true);
        Editor editor= new Editor(file);
        editor.getDocument().addDocumentListener(new EditDocumentListener(this));
        iframe.add(new JScrollPane(editor));
        iframe.addInternalFrameListener(this.iframeListener);
        desktop.add(iframe);
        iframe.show();
        try {
            iframe.setMaximum(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        tabPane.addTab(file.getName(), null, null, file.getAbsolutePath());
        tabPane.setSelectedIndex(tabPane.getTabCount()-1);
        currentFile = new EditFile(file, true, iframe, editor);
        editFiles.add(currentFile);
    }

    public JDesktopPane getDesktop() {
        return desktop;
    }
    public int getTabIndex(String tip) {
        for (int i = 0; i < this.tabPane.getTabCount(); ++i) {
            if (tabPane.getToolTipTextAt(i).equals(tip)) {
                return i;
            }
        }
        return -1;
    }

    public JTabbedPane getTabPane() {
        return tabPane;
    }

    public EditFile getCurrentFile() {
        return currentFile;
    }
    public void closeIFrame(JInternalFrame iframe) {
        EditFile closeFile = getEditFile(iframe);
        afterClose(closeFile);
        int index = getTabIndex(iframe.getTitle());
        getTabPane().remove(index);
        editFiles.remove(closeFile);
    }
    public EditFile getEditFile(JInternalFrame iframe) {
        for (EditFile openFile : editFiles) {
            if (openFile.getIframe().equals(iframe)) return openFile;
        }
        return null;
    }

    private void afterClose(EditFile closeFile) {
        int editFilesIndex = getEditFileIndex(closeFile);
        if (editFiles.size() == 1) {
            currentFile = null;
        } else {
            if (editFilesIndex == 0) {
                currentFile = editFiles.get(editFilesIndex+1);
            } else if (editFilesIndex == (editFiles.size()-1)) {
                currentFile = editFiles.get(editFiles.size()-2);
            } else {
                currentFile = editFiles.get(editFilesIndex-1);
            }
        }
    }
    private int getEditFileIndex(EditFile editFile) {
        for (int i = 0; i < editFiles.size(); ++i) {
            if (editFiles.get(i).equals(editFile)) return i;
        }
        return -1;
    }
    public void askSave(EditFile file) {
        if (!file.isSaved()) {
            int val = JOptionPane.showConfirmDialog(this, "是否保存？", "询问", JOptionPane.YES_NO_OPTION);
            if (JOptionPane.YES_OPTION == val) {
                saveFile(file);
            }
        }
    }

    public void saveFile(EditFile file) {
        FileUtil.writeFile(file.getFile(), file.getEditor().getText());
        file.setSaved(true);
    }
}
