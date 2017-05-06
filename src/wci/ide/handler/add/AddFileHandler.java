package wci.ide.handler.add;

import wci.ide.AddFrame;
import wci.ide.EditorFrame;
import wci.ide.exception.FileException;
import wci.ide.tree.ProjectTreeNode;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class AddFileHandler implements AddHandler {
    @Override
    public void afterAdd(EditorFrame editorFrame, AddFrame addFrame, Object data) {
        // run the code here after entering the file name
        try {
            ProjectTreeNode selectNode = editorFrame.getSelectNode();
            File folder = selectNode.getFile();
            if (!folder.isDirectory()) {
                ProjectTreeNode parent = (ProjectTreeNode) selectNode.getParent();
                selectNode = parent;
                folder = parent.getFile();
            }
            File newFile = new File(folder.getAbsoluteFile()+File.separator+data);
            if (newFile.exists()) {
                JOptionPane.showMessageDialog(addFrame, "文件已经存在");
                return;
            }
            newFile.createNewFile();
            editorFrame.reloadNode(selectNode);
            editorFrame.setEnabled(true);
            addFrame.setVisible(false);
        } catch (Exception e) {
            throw new FileException("create file error" + e.getMessage());
        }

    }
}
