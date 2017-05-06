package wci.ide;

import wci.ide.tree.TreeCreator;
import wci.ide.tree.TreeCreatorImpl;

public class PascalIDE {
    public static void main(String[] args) {
        TreeCreator treeCreator = new TreeCreatorImpl();
        EditorFrame editorFrame = new EditorFrame("PascalIDE", treeCreator);
        SpaceFrame spaceFrame = new SpaceFrame(editorFrame);
        spaceFrame.setVisible(true);
    }
}
