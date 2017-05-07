package wci.ide.ideimpl.util.edit;


import wci.ide.ideimpl.util.FileUtil;

import javax.swing.*;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Editor extends JTextPane {
    protected StyledDocument doc;
    private SimpleAttributeSet lineAttr = new SimpleAttributeSet();
    protected SyntaxFormatter formatter = new SyntaxFormatter("syntax-config/pascal.stx");
    private SimpleAttributeSet quotAttr = new SimpleAttributeSet();
    private int docChangeStart = 0;
    private int docChangeLength = 0;
    public Editor(File file) {
        this.setText(FileUtil.readFile(file));
        this.doc = getStyledDocument();
        this.setMargin(new Insets(3, 40, 0, 0));
        syntaxParse();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                syntaxParse();
            }
        });
    }
    public void syntaxParse() {
        try {
            Element root = doc.getDefaultRootElement();
            int cursorPos = this.getCaretPosition();
            int line = root.getElementIndex(cursorPos);
            Element para = root.getElement(line);
            int start = para.getStartOffset();
            if (start > docChangeStart)	{
                start = docChangeStart;
            }
            int length = para.getEndOffset() - start;
            if (length < docChangeLength) {
                length = docChangeLength + 1;
            }
            String s = doc.getText(start, length);
            String[] tokens = s.split("\\s+|\\.|\\(|\\)|\\{|\\}|\\[|\\]");
            int curStart = 0;
            boolean isQuot = false;
            for (String token : tokens) {
                int tokenPos = s.indexOf(token , curStart);
                if (isQuot && (token.endsWith("\"") || token.endsWith("\'"))) {
                    doc.setCharacterAttributes(start + tokenPos, token.length(), quotAttr, false);
                    isQuot = false;
                } else if (isQuot && !(token.endsWith("\"") || token.endsWith("\'"))) {
                    doc.setCharacterAttributes(start + tokenPos, token.length(), quotAttr, false);
                } else if ((token.startsWith("\"") || token.startsWith("\'"))
                        && (token.endsWith("\"") || token.endsWith("\'"))) {
                    doc.setCharacterAttributes(start + tokenPos, token.length(), quotAttr, false);
                } else if ((token.startsWith("\"") || token.startsWith("\'"))
                        && !(token.endsWith("\"") || token.endsWith("\'"))) {
                    doc.setCharacterAttributes(start + tokenPos, token.length(), quotAttr, false);
                    isQuot = true;
                } else {
                    formatter.setHighLight(doc , token , start + tokenPos, token.length());
                }
                curStart = tokenPos + token.length();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Element root = doc.getDefaultRootElement();
        int line = root.getElementIndex(doc.getLength());
        g.setColor(new Color(230, 230, 230));
        g.fillRect(0, 0, this.getMargin().left-10, getSize().height);
        g.setColor(new Color(40, 40, 40));
        for (int count = 0, j = 1; count <= line; ++count, ++j) {
            g.drawString(String.valueOf(j), 3, (int)((count+1)*1.5020* StyleConstants.getFontSize(lineAttr)));
        }
    }
}
class SyntaxFormatter {
    private Map<SimpleAttributeSet, ArrayList> attrMap = new HashMap<>();
    SimpleAttributeSet normalAttr = new SimpleAttributeSet();
    public SyntaxFormatter(String syntaxFile) {
        StyleConstants.setForeground(normalAttr, Color.black);
        try {
            Scanner scanner = new Scanner(new File(syntaxFile));
            int color = -1;
            ArrayList<String> patterns = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) {
                    if (patterns.size() > 0 && color > -1) {
                        SimpleAttributeSet attr = new SimpleAttributeSet();
                        StyleConstants.setForeground(attr, new Color(color));
                        attrMap.put(attr, patterns);
                    }
                    patterns = new ArrayList<>();
                    color = Integer.parseInt(line.substring(1), 16);
                } else {
                    if (line.trim().length() > 0) {
                        patterns.add(line.trim());
                    }
                }
            }
            if (patterns.size() > 0 && color > -1) {
                SimpleAttributeSet attr = new SimpleAttributeSet();
                StyleConstants.setForeground(attr, new Color(color));
                attrMap.put(attr, patterns);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public SimpleAttributeSet getNormalAttrbuteSet(){
        return normalAttr;
    }
    public void setHighLight(StyledDocument doc, String token, int start, int length) {
        SimpleAttributeSet currentAttrSet = null;
        outer :
        for (SimpleAttributeSet attr : attrMap.keySet()) {
            ArrayList patterns = attrMap.get(attr);
            for (Object pattern : patterns) {
                if (Pattern.matches((String) pattern, token.toUpperCase())) {
                    currentAttrSet = attr;
                    break outer;
                }
            }
        }
        if (currentAttrSet != null) {
            doc.setCharacterAttributes(start, length, currentAttrSet, false);
        } else {
            doc.setCharacterAttributes(start, length, normalAttr, false);
        }
    }
}

