package wci.frontend.pascal.parsers;

import wci.frontend.Token;
import wci.frontend.pascal.PascalParserTD;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;

import static wci.frontend.pascal.PascalErrorCode.MISSING_END;
import static wci.frontend.pascal.PascalTokenType.END;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.COMPOUND;

/**
 * CompoundStatementParser
 *
 * Parse a Pascal compound statement.
 */
public class CompoundStatementParser extends StatementParser {
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public CompoundStatementParser(PascalParserTD parent) {
        super(parent);
    }

    /**
     * Parse a compound statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token) throws Exception {
        token = nextToken();    // consume the BEGIN

        // Create the COMPOUND node.
        ICodeNode compoundNode = ICodeFactory.createICodeNode(COMPOUND);

        // Parse the statement list terminated by the END token.
        StatementParser statementParser = new StatementParser(this);
        statementParser.parseList(token, compoundNode, END, MISSING_END);

        return compoundNode;
    }
}
