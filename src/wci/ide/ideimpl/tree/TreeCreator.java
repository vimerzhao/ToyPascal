package wci.ide.ideimpl.tree;

import wci.ide.IDEFrame;

import javax.swing.*;
import java.io.File;

public interface TreeCreator {
    JTree createTree(IDEFrame ideFrame);
    ProjectTreeNode createNode(File folder);
}
