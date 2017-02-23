package wci.frontend.pascal.parsers;

import wci.frontend.EofToken;
import wci.frontend.Token;
import wci.frontend.TokenType;
import wci.frontend.pascal.PascalErrorCode;
import wci.frontend.pascal.PascalParserTD;
import wci.frontend.pascal.PascalTokenType;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;

import static wci.frontend.pascal.PascalErrorCode.MISSING_COLON;
import static wci.frontend.pascal.PascalErrorCode.UNEXPECTED_TOKEN;
import static wci.frontend.pascal.PascalTokenType.IDENTIFIER;
import static wci.frontend.pascal.PascalTokenType.SEMICOLON;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.LINE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.NO_OP;

/**
 * <h1>StatementParser</h1>
 *
 * <p>Parse a Pascal statement.</p>
 */
public class StatementParser extends PascalParserTD {
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public StatementParser(PascalParserTD parent) {
        super(parent);
    }

    /**
     * Parse a statement.
     * To be overridden by the specialized statement parser subclass.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token) throws Exception {
        ICodeNode statementNode = null;

        switch ((PascalTokenType) token.getType()) {
            case BEGIN: {
                CompoundStatementParser compoundParser = new CompoundStatementParser(this);
                statementNode = compoundParser.parse(token);
                break;
            }
            // An assignment statement begins with a variable's identifier.
            case IDENTIFIER: {
                AssignmentStatementParser assignmentParser = new AssignmentStatementParser(this);
                statementNode = assignmentParser.parse(token);
                break;
            }
            default: {
                statementNode = ICodeFactory.createICodeNode(NO_OP);
                break;
            }
        }

        // Set the current line number as an attribute.
        setLineNumber(statementNode, token);

        return statementNode;
    }


    protected void setLineNumber(ICodeNode node, Token token) {
        if (node != null) {
            node.setAttribute(LINE, token.getLineNumber());
        }
    }

    /**
     * Parse a statement list.
     * @param token the current token.
     * @param parentNode the parent node of the statement list.
     * @param terminator the token type of the node that terminates the list.
     * @param errorCode the error code if the terminator token if missing.
     * @throws Exception if an error occurred.
     */
    protected void parseList(Token token, ICodeNode parentNode,
                             PascalTokenType terminator,
                             PascalErrorCode errorCode) throws Exception {
        // Loop to parse each statement until the END token or the end of the source line.
        while (!(token instanceof EofToken) && (token.getType() != terminator)) {
            // Parse a statement.The parent node adopts the statement node.
            ICodeNode statementNode = parse(token);
            parentNode.addChild(statementNode);

            token = currentToken();
            TokenType tokenType = token.getType();

            // Look for the semicolon between statements.
            if (tokenType == SEMICOLON) {
                token = nextToken();    // consume the ;
            } else if (tokenType == IDENTIFIER) {
                // If at the start of the next assignment statement,
                // then missing a semicolon.
                errorHandler.flag(token, MISSING_COLON, this);
            } else if (tokenType != terminator) {
                // Unexpected token.
                errorHandler.flag(token, UNEXPECTED_TOKEN, this);
                token = nextToken();    // consume the unexpected token
            }
        }

        // Look for the terminator token.
        if (token.getType() == terminator) {
            token = nextToken();    // consume the terminator token
        } else {
            errorHandler.flag(token, errorCode, this);
        }
    }
}
