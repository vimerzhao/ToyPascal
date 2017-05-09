package wci.ide.ideimpl;

import javax.swing.*;

public class ConsolePane extends JScrollPane {
    private JTextArea info;
    public ConsolePane(JTextArea info) {
        super(info);
        this.info = info;
    }

    public void setInfo(String info) {
        this.info.setText(info);
    }
}
