package wci.ide;

import wci.ide.commons.AddInfo;
import wci.ide.commons.EditFile;
import wci.ide.commons.WorkSpace;
import wci.ide.handler.add.AddFileHandler;
import wci.ide.handler.add.AddFolderHandler;
import wci.ide.tree.ProjectTreeModel;
import wci.ide.tree.ProjectTreeNode;
import wci.ide.tree.TreeCreator;
import wci.ide.util.FileUtil;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.*;

public class EditorFrame extends JFrame {
    private JTabbedPane tabbedPane;     // tab title for multi file

    private Box box;                    // container of tabPane & desk
    private JDesktopPane desk;          // create a multi-file desktop container
    private JSplitPane editorSplitPane; // split edit zoom and info zoom
    private JTabbedPane windowTabPane;  //
    private JScrollPane infoPane;       // scrollable object, store infoArea
    private JScrollPane stackPane;      //
    private JTextArea infoArea;         // show info
    private JTextArea iCode;            // intermediate code
    private JTree stackTree;            //
    private JScrollPane treePane;       //
    private JSplitPane mainSplitPane;   //
    private JTree tree;                 //

    private JMenuBar menuBar;           //
    private JMenu fileMenu;             //
    private JMenu editMenu;             //
    private JMenu helpMenu;             //

    private JToolBar toolBar;           //
    private IFrameListener iframeListener;
    private WorkSpace workSpace;
    private TreeCreator treeCreator;
    private EditFile currentFile;
    private java.util.List<EditFile> openFiles = new ArrayList<>();
    private FileChooser fileChooser;

    private AddFrame addFrame;

    private Action fileNew = new AbstractAction("新建文件", new ImageIcon("images/fileNew.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            newFile();
        }
    };
    public void newFile() {
        AddInfo info = new AddInfo("文件名称：", this, new AddFileHandler());
        showAddFrame(info);
    }
    private void showAddFrame(AddInfo info) {
        setEnabled(false); // set edit-frame unable.
        addFrame = new AddFrame(info);
        addFrame.pack();
        addFrame.setVisible(true);
    }

    private Action folderNew = new AbstractAction("新建目录", new ImageIcon("images/folderNew.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            newFolder();
        }
    };
    public void newFolder() {
        AddInfo info = new AddInfo("目录名称：", this, new AddFolderHandler());
        showAddFrame(info);
    }

    private Action open = new AbstractAction("打  开", new ImageIcon("images/open.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectFile();
        }
    };
    public void selectFile() {
        fileChooser = new FileChooser(this);
    }

    private Action save = new AbstractAction("保  存", new ImageIcon("images/save.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action exit = new AbstractAction("退  出", new ImageIcon("images/exit.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };
    private Action copy = new AbstractAction("复  制", new ImageIcon("images/copy.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action cut = new AbstractAction("剪  切", new ImageIcon("images/cut.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action paste = new AbstractAction("粘  贴", new ImageIcon("images/paste.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action manual = new AbstractAction("用户指南") {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action about = new AbstractAction("关  于") {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action run = new AbstractAction("run", new ImageIcon("images/run.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action debug = new AbstractAction("debug", new ImageIcon("images/debug.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action breakpoint = new AbstractAction("breakpoint", new ImageIcon("images/breakpoint.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action step = new AbstractAction("step", new ImageIcon("images/step.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action go = new AbstractAction("go", new ImageIcon("images/resume.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action quit = new AbstractAction("quit", new ImageIcon("images/terminate.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    public EditorFrame(String title, TreeCreator treeCreator) {
        super(title);
        this.treeCreator = treeCreator;
        this.iframeListener = new IFrameListener(this);

    }

    public void initFrame(WorkSpace workSpace) {
        this.workSpace = workSpace;
        // set attribute
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("images/test.png").getImage());

        // init component
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBackground(Color.blue);
        desk = new JDesktopPane();
        desk.setBackground(Color.GRAY);
        box = new Box(BoxLayout.Y_AXIS);
        box.add(tabbedPane);
        box.add(desk);

        windowTabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        infoArea = new JTextArea("测试\n\n\n\n\n\n\n\n\n哈哈", 5, 50);
        infoPane = new JScrollPane(infoArea);
        infoArea.setEditable(false);
        windowTabPane.add(infoPane, "控制台");
        stackTree = new JTree();
        stackPane = new JScrollPane(stackTree);
        windowTabPane.add(stackPane, "调用栈");
        iCode = new JTextArea("中间代码.......\n................\n");
        iCode.setEditable(false);
        windowTabPane.add(iCode, "中间代码");
        editorSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, box, windowTabPane);
        editorSplitPane.setDividerSize(3);
        editorSplitPane.setDividerLocation(500);
        add(editorSplitPane);

        tree = treeCreator.createTree(this);
        treePane = new JScrollPane(tree);
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,treePane, editorSplitPane);
        mainSplitPane.setDividerLocation(200);
        mainSplitPane.setDividerSize(4);
        add(mainSplitPane);

        UIManager.put("Menu.font", new Font("黑体", Font.PLAIN, 16));
        UIManager.put("MenuItem.font", new Font("黑体", Font.PLAIN, 14));
        menuBar = new JMenuBar();
        fileMenu = new JMenu("文件");
        editMenu = new JMenu("编辑");
        helpMenu = new JMenu("帮助");

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMargin(new Insets(5, 10, 5, 5));
        add(toolBar, BorderLayout.NORTH);

        addListeners();
    }

    private void addListeners() {
        fileMenu.add(fileNew).setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
        fileMenu.add(folderNew).setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
        fileMenu.add(open).setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK));
        fileMenu.add(save).setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
        fileMenu.add(exit);

        editMenu.add(copy).setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
        editMenu.add(cut).setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
        editMenu.add(paste).setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));

        helpMenu.add(manual);
        helpMenu.add(about);

        toolBar.add(run).setToolTipText("运行");
        toolBar.add(debug).setToolTipText("调试");
        toolBar.add(breakpoint).setToolTipText("断点");
        toolBar.add(step).setToolTipText("单步");
        toolBar.add(go).setToolTipText("继续");
        toolBar.add(quit).setToolTipText("退出");

        tabbedPane.addChangeListener(new TabListener(this));
    }

    public WorkSpace getWorkSpace() {
        return workSpace;
    }

    public ProjectTreeNode getSelectNode() {
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            ProjectTreeNode selectNode = (ProjectTreeNode) path.getLastPathComponent();
            return selectNode;
        }
        return null;
    }


    public JTree getTree() {
        return tree;
    }
    public void reloadNode(ProjectTreeNode selectNode) {
        if (selectNode == null) return;
        ProjectTreeModel model = (ProjectTreeModel) getTree().getModel();
        model.reload(selectNode, treeCreator);
    }

    public void openFile(File file) {
        if (currentFile != null) {
            if (file.equals(currentFile.getFile())) {
                return;
            }
        }
        EditFile openedFile = getOpenFile(file);
        if (openedFile != null) {
            openExistFile(openedFile, file);
            return;
        }
        openNewFile(file);
    }

    public void openNewFile(File file) {
        setTitle("PascalIDE("+file.getAbsolutePath()+")");
        JInternalFrame iframe= new JInternalFrame(file.getAbsolutePath(), true, true, true, true);
        EditPane editPane = new EditPane(file);
        editPane.getDocument().addDocumentListener(new EditDocumentListener(this));
        iframe.add(new JScrollPane(editPane));
        iframe.addInternalFrameListener(this.iframeListener);
        desk.add(iframe);
        iframe.show();
        try {
            iframe.setMaximum(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        tabbedPane.addTab(file.getName(), null, null, file.getAbsolutePath());
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
        currentFile = new EditFile(file, true, iframe, editPane);
        openFiles.add(currentFile);
    }

    public JInternalFrame getIFrame(String title) {
        JInternalFrame[] iframes = desk.getAllFrames();
        for (JInternalFrame iframe : iframes) {
            if (iframe.getTitle().equals(title)) return iframe;
        }
        return null;
    }
    public EditFile getEditFile(JInternalFrame iframe) {
        for (EditFile openFile : openFiles) {
            if (openFile.getIframe().equals(iframe)) return openFile;
        }
        return null;
    }

    public void setCurrentFile(EditFile currentFile) {
        this.currentFile = currentFile;
    }

    public JDesktopPane getDesk() {
        return desk;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
    public int getTabIndex(String tip) {
        for (int i = 0; i < this.tabbedPane.getTabCount(); ++i) {
            if (tabbedPane.getToolTipTextAt(i).equals(tip)) {
                return i;
            }
        }
        return -1;
    }

    public void openExistFile(EditFile openedFile, File willOpenFile) {
        tabbedPane.setSelectedIndex(getFileIndex(willOpenFile));
        showIFrame(openedFile.getIframe());
        this.currentFile = openedFile;
        openFiles.add(openedFile);
    }
    public void askSave(EditFile file) {
        if (!file.isSaved()) {
            int val = JOptionPane.showConfirmDialog(this, "是否保存？", "询问", JOptionPane.YES_NO_OPTION);
            if (JOptionPane.YES_OPTION == val) {
                saveFile(file);
            }
        }
    }

    public void saveFile(EditFile file) {
        FileUtil.writeFile(file.getFile(), file.getEditPane().getText());
        file.setSaved(true);
    }
    public void closeIFrame(JInternalFrame iframe) {
        EditFile closeFile = getEditFile(iframe);
        afterClose(closeFile);
        int index = getTabIndex(iframe.getTitle());
        getTabbedPane().remove(index);
        openFiles.remove(closeFile);
    }
    private void afterClose(EditFile closeFile) {
        int openFilesIndex = getEditFileIndex(closeFile);
        if (openFiles.size() == 1) {
            currentFile = null;
        } else {
            if (openFilesIndex == 0) {
                currentFile = openFiles.get(openFilesIndex+1);
            } else if (openFilesIndex == (openFiles.size()-1)) {
                currentFile = openFiles.get(openFiles.size()-2);
            } else {
                currentFile = openFiles.get(openFilesIndex-1);
            }
        }
    }

    private int getEditFileIndex(EditFile editFile) {
        for (int i = 0; i < openFiles.size(); ++i) {
            if (openFiles.get(i).equals(editFile)) return i;
        }
        return -1;
    }

    public void showIFrame(JInternalFrame iframe) {
        try {
            iframe.setSelected(true);
            iframe.toFront();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }
    public EditFile getCurrentFile() {
        return currentFile;
    }
    private EditFile getOpenFile(File file) {
        for (EditFile openFile : openFiles) {
            if (openFile.getFile().equals(file)) {
                return openFile;
            }
        }
        return null;
    }
    private int getFileIndex(File file) {
        EditFile openFile = getEditFile(file);
        if (openFile == null) {
            return -1;
        }
        return getTabIndex(openFile.getIframe().getToolTipText());
    }
    private EditFile getEditFile(File file) {
        for (EditFile openFile : openFiles) {
            if (openFile.getFile().equals(file)) return openFile;
        }
        return null;
    }

}

class FileChooser extends JFileChooser {
    private EditorFrame editorFrame;
    public FileChooser(EditorFrame editorFrame) {
        super(editorFrame.getWorkSpace().getFolder());
        this.editorFrame = editorFrame;
        showOpenDialog(editorFrame);
    }

    @Override
    public void approveSelection() {
        File file = getSelectedFile();
        this.editorFrame.getTree().setSelectionPath(null);
        this.editorFrame.openFile(file);
        super.approveSelection();
    }
}
