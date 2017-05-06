package wci.ide;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EditDocumentListener implements DocumentListener{
    private EditorFrame editorFrame;
    public EditDocumentListener(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;
    }
    @Override
    public void insertUpdate(DocumentEvent e) {
        editorFrame.getCurrentFile().setSaved(false);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {}

    @Override
    public void changedUpdate(DocumentEvent e) {}
}
