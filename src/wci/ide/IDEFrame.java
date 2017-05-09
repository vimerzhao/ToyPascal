package wci.ide;

import wci.ide.ideimpl.*;
import wci.ide.ideimpl.tree.ProjectTreeNode;
import wci.ide.ideimpl.tree.TreeCreatorImpl;
import wci.ide.ideimpl.util.add.AddFileHandler;
import wci.ide.ideimpl.util.add.AddFolderHandler;
import wci.ide.ideimpl.util.add.AddFrame;
import wci.ide.ideimpl.util.add.AddInfo;
import wci.ide.ideimpl.util.run.RunProcess;
import wci.util.ParseTreePrinter;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.Enumeration;

import static wci.ide.IDEControl.*;

public class IDEFrame extends JFrame {
    private JSplitPane editSplitPane;
    private EditPane editPane;
    private JTabbedPane debugPane;
    private CallStackPane callStackPane;
    private ConsolePane consolePane;
    private FileBrowserPane fileBrowserPane;
    private ICodePane iCodePane;
    private OutputPane outputPane;
    private JSplitPane fileSplitPane;

    // menu bar
    private JMenuBar menuBar;
    private JMenu fileMenu;

    // tool bar
    private JToolBar toolBar;

    private AddFrame addFrame;


    private Action newFile = new AbstractAction("新建文件", new ImageIcon("images/fileNew.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            createNewFile();
        }
    };
    private void createNewFile() {
        AddInfo info = new AddInfo("新建文件", this, new AddFileHandler());
        showAddFrame(info);
    }
    private void showAddFrame(AddInfo info) {
        setEnabled(false);
        addFrame = new AddFrame(info);
        addFrame.pack();
        addFrame.setVisible(true);
    }
    private Action newFolder = new AbstractAction("新建目录", new ImageIcon("images/folderNew.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            createNewFolder();
        }
    };
    public void createNewFolder() {
        AddInfo info = new AddInfo("目录名称", this, new AddFolderHandler());
        showAddFrame(info);
    }


    private Action open = new AbstractAction("打开文件", new ImageIcon("images/open.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectFile();
        }
    };
   public void selectFile() {
       new FileChooser(this);
    }


    private Action exit = new AbstractAction("退   出") {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };

    private Action run = new AbstractAction("run", new ImageIcon("images/run.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            runProgram();
        }
    };

    private void runProgram() {
        if (editPane.getCurrentFile() == null) {
            JOptionPane.showMessageDialog(null, "未选择文件");
            return;
        }
        // save file first!
        editPane.saveFile(editPane.getCurrentFile());
        String path = editPane.getCurrentFile().getFile().getAbsolutePath();
        String result = RunProcess.run(path);
        System.out.println(result);
        String[] strings = result.split("\n");
        StringBuilder outputBuilder = new StringBuilder();
        StringBuilder consoleBuilder= new StringBuilder();
        StringBuilder iCodeBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; ++i) {
            if (strings[i].startsWith(SYNTAX_TAG)) {
               consoleBuilder.append(strings[i].substring(SYNTAX_TAG.length())).append('\n');
            } else if (strings[i].startsWith(PARSER_TAG)){
                consoleBuilder.append(strings[i].substring(PARSER_TAG.length())).append('\n');
            } else if (strings[i].startsWith(INTERPRETER_TAG)) {
                consoleBuilder.append(strings[i].substring(INTERPRETER_TAG.length())).append('\n');
            } else if (strings[i].startsWith(ParseTreePrinter.BEGIN_ICODE)) {
                while (!strings[++i].startsWith(ParseTreePrinter.END_ICODE)) {
                    iCodeBuilder.append(strings[i]).append('\n');
                }
            }else if (strings[i].startsWith(LISTING_TAG)) {

            } else {
                outputBuilder.append(strings[i]).append('\n');
            }
        }
        consolePane.setInfo(consoleBuilder.toString());
        outputPane.setOutput(outputBuilder.toString());
        iCodePane.setICode(iCodeBuilder.toString());

    }
    private Action step = new AbstractAction("debug", new ImageIcon("images/step.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action go = new AbstractAction("go", new ImageIcon("images/resume.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action debug = new AbstractAction("debug", new ImageIcon("images/debug.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    private Action terminate = new AbstractAction("quit", new ImageIcon("images/terminate.gif")) {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    public IDEFrame(String title) {
        super(title);
        initFrame();
    }
    public void initFrame() {
        InitGlobalFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
        UIManager.put("TextArea.font", font);

        // menu
        menuBar = new JMenuBar();
        fileMenu = new JMenu("文件");
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
        // toolbar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMargin(new Insets(5, 5, 5, 5));
        add(toolBar, BorderLayout.NORTH);

        addListeners();
        toolBar.getComponent(1).setEnabled(false);
        toolBar.getComponent(2).setEnabled(false);
        toolBar.getComponent(3).setEnabled(false);
        toolBar.getComponent(4).setEnabled(false);

        // edit pane
        editPane = new EditPane(BoxLayout.Y_AXIS);



        // debug pane
        debugPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        debugPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        consolePane = new ConsolePane(new JTextArea());
        callStackPane = new CallStackPane();
        iCodePane = new ICodePane(new JTextArea());
        outputPane = new OutputPane(new JTextArea());
        debugPane.add(consolePane, "控制台");
        debugPane.add(outputPane, "输出");
        //debugPane.add(callStackPane, "调用栈");
        debugPane.add(iCodePane, "中间代码");
        editSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editPane, debugPane);
        editSplitPane.setDividerSize(4);
        editSplitPane.setDividerLocation(500);

        add(editSplitPane);

        // file browser
        fileBrowserPane = new TreeCreatorImpl().createTree(this);
        fileSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(fileBrowserPane), editSplitPane);
        fileSplitPane.setDividerSize(4);
        fileSplitPane.setDividerLocation(300);

        add(fileSplitPane);

    }

    private void addListeners() {
        fileMenu.add(newFile).setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
        fileMenu.add(newFolder);
        fileMenu.add(open);
        fileMenu.add(exit);
        toolBar.add(run).setToolTipText("运行");
        toolBar.add(debug).setToolTipText("调试");
        toolBar.add(step).setToolTipText("单步调试");
        toolBar.add(go).setToolTipText("恢复运行");
        toolBar.add(terminate).setToolTipText("退出调试");
    }

    public ProjectTreeNode getSelectNode() {
        return fileBrowserPane.getSelectNode();
    }
    public void reloadNode(ProjectTreeNode selectNode) {
        fileBrowserPane.reloadNode(selectNode);
    }

    public void openFile(File file) {
        setTitle("PascalIDE("+file.getAbsolutePath()+")");
        editPane.openFile(file);
    }

    public FileBrowserPane getFileBrowserPane() {
        return fileBrowserPane;
    }

    private void InitGlobalFont(Font font) {
        FontUIResource fontRes = new FontUIResource(font);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys();
             keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
    }
}
class FileChooser extends JFileChooser {
    private IDEFrame ideFrame;

    public FileChooser(IDEFrame ideFrame) {
        super("./");
        this.ideFrame = ideFrame;
        showOpenDialog(ideFrame);
    }

    @Override
    public void approveSelection() {
        File file = getSelectedFile();
        this.ideFrame.getFileBrowserPane().setSelectionPath(null);
        this.ideFrame.openFile(file);
        super.approveSelection();
    }


}
