import wci.ide.IDEFrame;
import wci.ide.ideimpl.SpaceFrame;

import java.awt.*;

public class PascalIDE {
    public static void main(String[] args) {
        EventQueue.invokeLater(()->{
            IDEFrame ideFrame = new IDEFrame("IDE");
            SpaceFrame spaceFrame = new SpaceFrame(ideFrame);
            spaceFrame.setVisible(true);
        });
    }
}
