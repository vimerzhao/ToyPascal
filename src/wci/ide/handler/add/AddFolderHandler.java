package wci.ide.handler.add;

import wci.ide.AddFrame;
import wci.ide.EditorFrame;
import wci.ide.commons.AddInfo;
import wci.ide.exception.FileException;
import wci.ide.tree.ProjectTreeNode;

import javax.swing.*;
import java.io.File;

public class AddFolderHandler implements AddHandler {
    @Override
    public void afterAdd(EditorFrame editorFrame, AddFrame addFrame, Object data) {
        try {
            ProjectTreeNode selectNode = editorFrame.getSelectNode();
            File folder = selectNode.getFile();
            if (!folder.isDirectory()) {
                ProjectTreeNode parent = (ProjectTreeNode) selectNode.getParent();
                selectNode = parent;
                folder = parent.getFile();
            }
            File newFolder = new File(folder.getAbsoluteFile()+File.separator+data);
            if (newFolder.exists()) {
                JOptionPane.showMessageDialog(addFrame, "目录已经存在");
                return;
            }
            newFolder.mkdir();
            editorFrame.reloadNode(selectNode);
            editorFrame.setEnabled(true);
            addFrame.setVisible(false);
        } catch (Exception e) {
            throw new FileException("create folder error:" + e.getMessage());
        }
    }
}
