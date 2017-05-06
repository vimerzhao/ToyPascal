package wci.ide.commons;

import wci.ide.EditorFrame;
import wci.ide.handler.add.AddHandler;

public class AddInfo {
    private String info;// file name or folder name
    private EditorFrame editorFrame; // frame
    private AddHandler handler;// handler class after click add button
    public AddInfo(String info, EditorFrame editorFrame, AddHandler handler) {
        this.info = info;
        this.editorFrame = editorFrame;
        this.handler = handler;
    }

    public AddHandler getHandler() {
        return handler;
    }

    public void setHandler(AddHandler handler) {
        this.handler = handler;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public EditorFrame getEditorFrame() {
        return editorFrame;
    }

    public void setEditorFrame(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;
    }
}
