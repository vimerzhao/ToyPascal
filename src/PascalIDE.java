import wci.ide.IDEFrame;

import java.awt.*;

public class PascalIDE {
    public static void main(String[] args) {
        EventQueue.invokeLater(()->{
            IDEFrame ideFrame = new IDEFrame("IDE");
            ideFrame.setVisible(true);
        });
    }
}
