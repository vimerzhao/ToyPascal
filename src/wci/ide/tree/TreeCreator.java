package wci.ide.tree;

import wci.ide.EditorFrame;

import javax.swing.*;
import java.io.File;

public interface TreeCreator {
    JTree createTree(EditorFrame editorFrame);
    ProjectTreeNode createNode(File folder);
}
