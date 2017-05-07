package wci.ide.ideimpl.util.edit;


import wci.frontend.EofToken;
import wci.frontend.FrontendFactory;
import wci.frontend.Parser;
import wci.frontend.Source;
import wci.frontend.pascal.PascalTokenType;
import wci.ide.ideimpl.util.FileUtil;

import javax.swing.*;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Editor extends JTextPane {
    protected StyledDocument doc;
    private SimpleAttributeSet lineAttr = new SimpleAttributeSet();
    protected SyntaxFormatter formatter = new SyntaxFormatter("colorscheme/pascal.stx");
    private int docChangeStart = 0;
    private int docChangeLength = 0;
    public Editor(File file) {
        this.setText(FileUtil.readFile(file));
        this.setBackground(new Color(0xEB, 0xEB, 0xEB));
        this.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
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
            int curStart = 0;

            // reuse the code of frontend package
            Source source = new Source(
                    new BufferedReader(new InputStreamReader(
                            new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)))));
            Parser parser = FrontendFactory.createParser("Pascal", "top-down", source);


            while (!(parser.nextToken() instanceof EofToken)) {
                // attention: parser cannot find comment.

                if (s.charAt(curStart+1) == '{') {
                    ++curStart;
                    while (s.charAt(curStart) != '}') ++curStart;
                }
                String token = parser.currentToken().getText();

                PascalTokenType tokenType = (PascalTokenType) parser.currentToken().getType();
                int tokenPos =  s.indexOf(token, curStart);

                formatter.setHighLight(doc, tokenType, start+tokenPos, token.length());
                curStart = tokenPos+token.length();

            }
        } catch (Exception ex) {
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
                        System.out.println(color);
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

    public void setHighLight(StyledDocument doc, PascalTokenType tokenType, int start, int length) {
        SimpleAttributeSet currentAttrSet = null;
        outer :
        for (SimpleAttributeSet attr : attrMap.keySet()) {
            ArrayList patterns = attrMap.get(attr);
            for (Object pattern : patterns) {
                if (pattern.equals(tokenType.toString())) {
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

