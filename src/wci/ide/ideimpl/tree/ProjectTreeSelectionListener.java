package wci.ide.ideimpl.tree;

import wci.ide.IDEFrame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProjectTreeSelectionListener extends MouseAdapter {
    private IDEFrame ideFrame;
    public ProjectTreeSelectionListener(IDEFrame ideFrame) {
        this.ideFrame = ideFrame;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ProjectTreeNode selectNode = ideFrame.getSelectNode();
        if (selectNode == null) return;
        if (selectNode.getFile().isDirectory()) return;
        this.ideFrame.openFile(selectNode.getFile());
    }
}
