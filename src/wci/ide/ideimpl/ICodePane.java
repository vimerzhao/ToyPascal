package wci.ide.ideimpl;

import javax.swing.*;

public class ICodePane extends JScrollPane {
    private JTextArea iCode;

    public ICodePane(JTextArea iCode) {
        super(iCode);
        this.iCode = iCode;
    }

    public void setICode(String iCode) {
        this.iCode.setText(iCode);
    }
}
