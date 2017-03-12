package wci.frontend.pascal.parsers;

import wci.frontend.Token;
import wci.frontend.pascal.PascalParserTD;
import wci.frontend.pascal.PascalTokenType;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.TypeSpec;
import wci.intermediate.icodeimpl.ICodeNodeTypeImpl;
import wci.intermediate.symtabimpl.Predefined;
import wci.intermediate.typeimpl.TypeChecker;

import java.util.EnumSet;

import static wci.frontend.pascal.PascalErrorCode.INCOMPATIBLE_TYPES;
import static wci.frontend.pascal.PascalErrorCode.MISSING_DO;
import static wci.frontend.pascal.PascalTokenType.DO;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.LOOP;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.TEST;

public class WhileStatementParser extends StatementParser {
    /**
     * Constructor.
     *
     * @param parent the parent parser.
     */
    public WhileStatementParser(PascalParserTD parent) {
        super(parent);
    }

    // Synchronization set for DO.
    private static final EnumSet<PascalTokenType> DO_SET =
            StatementParser.STMT_START_SET.clone();
    static {
        DO_SET.add(DO);
        DO_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    }

    /**
     * Parse a WHILE statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    @Override
    public ICodeNode parse(Token token) throws Exception {
        token = nextToken();    // consume the WHILE

        // Create LOOP,TEST,and NOT nodes.
        ICodeNode loopNode = ICodeFactory.createICodeNode(LOOP);
        ICodeNode breakNode = ICodeFactory.createICodeNode(TEST);
        ICodeNode notNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);

        // The LOOP node adopts the TEST node as its first child.
        // The TEST node adopts the NOT node as its only child.
        loopNode.addChild(breakNode);
        breakNode.addChild(notNode);

        // Parse the expression.
        // The NOT node adopts the expression subtree as its only child.
        ExpressionParser expressionParser = new ExpressionParser(this);
        ICodeNode exprNode = expressionParser.parse(token);
        notNode.addChild(exprNode);

        // Type check: The test expression must be boolean.
        TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec() : Predefined.undefinedType;
        if (!TypeChecker.isBoolean(exprType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }

        // Synchronize at the DO.
        token = synchronize(DO_SET);
        if (token.getType() == DO) {
            token = nextToken();    // consume the DO
        } else {
            errorHandler.flag(token, MISSING_DO, this);
        }

        // Parse the statement.
        // The LOOP node adopts the statement subtree as its second child.
        StatementParser statementParser = new StatementParser(this);
        loopNode.addChild(statementParser.parse(token));

        return loopNode;
    }
}