package wci.frontend.pascal.parsers;

import wci.frontend.Token;
import wci.frontend.pascal.PascalParserTD;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.TypeSpec;
import wci.intermediate.symtabimpl.Predefined;
import wci.intermediate.typeimpl.TypeChecker;

import static wci.frontend.pascal.PascalErrorCode.INCOMPATIBLE_TYPES;
import static wci.frontend.pascal.PascalErrorCode.MISSING_UNTIL;
import static wci.frontend.pascal.PascalTokenType.UNTIL;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.LOOP;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.TEST;

public class RepeatStatementParser extends StatementParser {
    /**
     * Constructor.
     *
     * @param parent the parent parser.
     */
    public RepeatStatementParser(PascalParserTD parent) {
        super(parent);
    }

    /**
     * Parse a REPEAT statement.
     *
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token) throws Exception {
        token = nextToken();    // consume the REPEAT

        // Create the LOOP and TEST nodes.
        ICodeNode loopNode = ICodeFactory.createICodeNode(LOOP);
        ICodeNode testNode = ICodeFactory.createICodeNode(TEST);

        // Parse the statement list terminated by the UNTIL token.
        // The LOOP node is the parent of the statement subtree.
        StatementParser statementParser = new StatementParser(this);
        statementParser.parseList(token, loopNode, UNTIL, MISSING_UNTIL);
        token = currentToken();

        // Parse the expression.
        // The TEST node adopts the expression subtree as its only child.
        ExpressionParser expressionParser = new ExpressionParser(this);
        ICodeNode exprNode = expressionParser.parse(token);
        testNode.addChild(exprNode);
        loopNode.addChild(testNode);

        // Type check: The test expression must be boolean.
        TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec() : Predefined.undefinedType;
        if (!TypeChecker.isBoolean(exprType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }
        return loopNode;
    }
}
