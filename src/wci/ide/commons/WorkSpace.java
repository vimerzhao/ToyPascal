package wci.ide.commons;

import wci.ide.EditorFrame;

import java.io.File;

public class WorkSpace {
    private File folder;            // directory for workspace
    private EditorFrame editorFrame;// edit frame for workspace
    public WorkSpace(File folder, EditorFrame editorFrame) {
        this.folder = folder;
        this.editorFrame = editorFrame;
    }

    public EditorFrame getEditorFrame() {
        return editorFrame;
    }

    public void setEditorFrame(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }
}
