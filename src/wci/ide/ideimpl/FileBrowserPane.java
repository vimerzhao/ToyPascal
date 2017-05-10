package wci.ide.ideimpl;

import wci.ide.ideimpl.tree.ProjectTreeModel;
import wci.ide.ideimpl.tree.ProjectTreeNode;
import wci.ide.ideimpl.tree.TreeCreatorImpl;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;

public class FileBrowserPane extends JTree {
    public FileBrowserPane(DefaultTreeModel treeModel) {
        super(treeModel);
        this.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
    }

    public ProjectTreeNode getSelectNode() {
        TreePath path = this.getSelectionPath();
        if (path != null) {
            ProjectTreeNode selectNode = (ProjectTreeNode) path.getLastPathComponent();
            return selectNode;
        }
        return null;
    }

    public void reloadNode(ProjectTreeNode selectNode) {
        if (selectNode == null) return;
        ProjectTreeModel model = (ProjectTreeModel) getModel();
        model.reload(selectNode, new TreeCreatorImpl());
    }
}

