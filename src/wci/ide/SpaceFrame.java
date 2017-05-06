package wci.ide;

import wci.ide.commons.WorkSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SpaceFrame extends JFrame {
    private JPanel mainPanel;
    private JLabel infoLabel;
    private JPanel choosePanel;
    private JLabel workTextLabel;
    private JTextField pathText;
    private JButton chooseButton;
    private JPanel buttonPanel;
    private JButton confirmButton;
    private JButton cancleButton;
    private SpaceChooser chooser;
    private File folder;

    public SpaceFrame(EditorFrame editorFrame) {
        mainPanel = new JPanel();
        infoLabel = new JLabel("请选择工作空间");
        choosePanel = new JPanel();
        workTextLabel = new JLabel("工作空间");
        pathText = new JTextField("", 40);
        chooseButton = new JButton("选择");
        buttonPanel = new JPanel();
        confirmButton = new JButton("确定");
        cancleButton = new JButton("取消");
        chooser = new SpaceChooser(this);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(infoLabel);
        choosePanel.setLayout(new BoxLayout(choosePanel, BoxLayout.X_AXIS));
        chooseButton.addActionListener(new ChooseButtonListener(chooser));
        pathText.setEnabled(false);
        choosePanel.add(workTextLabel);
        choosePanel.add(pathText);
        choosePanel.add(chooseButton);
        mainPanel.add(choosePanel);

        confirmButton.setEnabled(false);
        confirmButton.addActionListener(new ConfirmButtonListener(this, editorFrame));

        buttonPanel.add(confirmButton);
        buttonPanel.add(new Label("            "));
        buttonPanel.add(cancleButton);

        cancleButton.addActionListener(e->System.exit(0));// Amazing lambda!!

        mainPanel.add(buttonPanel);
        add(mainPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(100, 200);
        setResizable(false);
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }

    public JTextField getPathText() {
        return pathText;
    }

    public JButton getConfirmButton() {
        return confirmButton;
    }
}
class ConfirmButtonListener implements ActionListener {
    private SpaceFrame spaceFrame;
    private EditorFrame editorFrame;
    public ConfirmButtonListener(SpaceFrame spaceFrame, EditorFrame editorFrame) {
        this.spaceFrame = spaceFrame;
        this.editorFrame = editorFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        editorFrame.initFrame(new WorkSpace(spaceFrame.getFolder(), editorFrame));
        editorFrame.setVisible(true);
        spaceFrame.setVisible(false);
    }
}

class ChooseButtonListener implements ActionListener {
    private JFileChooser chooser;
    public ChooseButtonListener(JFileChooser chooser) {
        this.chooser = chooser;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(null);//show file chooser
    }
}
class SpaceChooser extends JFileChooser {
    private SpaceFrame spaceFrame;
    public SpaceChooser(SpaceFrame spaceFrame) {
        super("./");
        this.spaceFrame = spaceFrame;
    }

    @Override
    public void approveSelection() {
        super.approveSelection();
        File folder = getSelectedFile();
        spaceFrame.setFolder(folder);
        spaceFrame.getPathText().setText(folder.getAbsolutePath());
        spaceFrame.getConfirmButton().setEnabled(true);
    }
}
