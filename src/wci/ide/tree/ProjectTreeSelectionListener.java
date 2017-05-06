package wci.ide.tree;

import wci.ide.EditorFrame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProjectTreeSelectionListener extends MouseAdapter {
    private EditorFrame editorFrame;
    public ProjectTreeSelectionListener(EditorFrame editorFrame) {
        this.editorFrame = editorFrame;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ProjectTreeNode selectNode = editorFrame.getSelectNode();
        if (selectNode == null) return;
        if (selectNode.getFile().isDirectory()) return;
        this.editorFrame.openFile(selectNode.getFile());
    }
}
