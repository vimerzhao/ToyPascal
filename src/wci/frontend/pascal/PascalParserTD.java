package wci.frontend.pascal;

import wci.frontend.EofToken;
import wci.frontend.Parser;
import wci.frontend.Scanner;
import wci.frontend.Token;
import wci.frontend.pascal.parsers.ProgramParser;
import wci.intermediate.symtabimpl.Predefined;
import wci.message.Message;

import java.util.EnumSet;

import static wci.frontend.pascal.PascalErrorCode.IO_ERROR;
import static wci.frontend.pascal.PascalErrorCode.UNEXPECTED_TOKEN;
import static wci.message.MessageType.PARSER_SUMMARY;

/**
 * PascalParserTD
 * <p>
 * The top-down Pascal parser.
 */
public class PascalParserTD extends Parser {
    protected static PascalErrorHandler errorHandler = new PascalErrorHandler();

    /**
     * Constructor.
     *
     * @param scanner the scanner to be used with this parser.
     */
    public PascalParserTD(Scanner scanner) {
        super(scanner);
    }

    /**
     * Constructor for subclass.
     *
     * @param parent the parent parser.
     */
    public PascalParserTD(PascalParserTD parent) {
        super(parent.getScanner());
    }


    /**
     * Parse a Pascal source program and generate the symbol table and the intermediate code.
     * and the intermediate code.
     *
     * @throws Exception if an error occurred.
     */
    @Override
    public void parse() throws Exception {
        long startTime = System.currentTimeMillis();
        Predefined.initialize(symTabStack);

        try {
            Token token = nextToken();
            // Parse a program
            ProgramParser programParser = new ProgramParser(this);
            programParser.parse(token, null);
            token = currentToken();

            // Send the parser summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime) / 1000f;
            sendMessage(new Message(PARSER_SUMMARY,
                    new Number[]{token.getLineNumber(), getErrorCount(), elapsedTime}));
        } catch (java.io.IOException ex) {
            errorHandler.abortTranslation(IO_ERROR, this);
        }
    }

    /**
     * Return the number of syntax errors found by the parser.
     *
     * @return the error count.
     */
    @Override
    public int getErrorCount() {
        return errorHandler.getErrorCount();
    }

    /**
     * Synchronize the parser.
     *
     * @param syncSet the set of token types for synchronizing the parser.
     * @return the token where the parser has synchronized.
     * @throws Exception if an error occurred.
     */
    public Token synchronize(EnumSet syncSet) throws Exception {
        Token token = currentToken();

        // If the current token is not in the synchronization set,
        // the it is unexpected and the parser must recover.
        if (!syncSet.contains(token.getType())) {
            // Flag the unexpected token.
            errorHandler.flag(token, UNEXPECTED_TOKEN, this);

            // Recover by skipping tokens that are not in the synchronization set.
            do {
                token = nextToken();
            } while (!(token instanceof EofToken) && !syncSet.contains(token.getType()));
        }

        return token;
    }
}
