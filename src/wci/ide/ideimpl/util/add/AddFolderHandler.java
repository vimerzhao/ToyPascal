package wci.ide.ideimpl.util.add;

import wci.ide.IDEFrame;
import wci.ide.ideimpl.tree.ProjectTreeNode;

import javax.swing.*;
import java.io.File;

public class AddFolderHandler implements AddHandler {
    @Override
    public void afterAdd(IDEFrame ideFrame, AddFrame addFrame, Object data) {
        try {
            ProjectTreeNode selectNode = ideFrame.getSelectNode();
            File folder = selectNode.getFile();
            if (!folder.isDirectory()) {
                ProjectTreeNode parent = (ProjectTreeNode) selectNode.getParent();
                selectNode = parent;
                folder = parent.getFile();
            }
            File newFolder = new File(folder.getAbsoluteFile() + File.separator + data);
            if (newFolder.exists()) {
                JOptionPane.showMessageDialog(addFrame, "目录已经存在");
                return;
            }
            newFolder.mkdir();
            ideFrame.reloadNode(selectNode);
            ideFrame.setEnabled(true);
            addFrame.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
