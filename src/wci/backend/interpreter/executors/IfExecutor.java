package wci.backend.interpreter.executors;

import wci.backend.interpreter.Executor;
import wci.intermediate.ICodeNode;

import java.util.List;

public class IfExecutor extends StatementExecutor {
    /**
     * Constructor.
     *
     * @param parent executor.
     */
    public IfExecutor(Executor parent) {
        super(parent);
    }

    /**
     * Execute an IF statement.
     *
     * @param node the root node of the statement.
     * @return null.
     */
    @Override
    public Object execute(ICodeNode node) {
        // Get the IF node's children.
        List<ICodeNode> children = node.getChildren();
        ICodeNode exprNode = children.get(0);
        ICodeNode thenStmtNode = children.get(1);
        ICodeNode elseStmtNode = children.size() > 2 ? children.get(2) : null;

        ExpressionExecutor expressionExecutor = new ExpressionExecutor(this);
        StatementExecutor statementExecutor = new StatementExecutor(this);

        // Evaluate the expression to determine which statement to execute.
        boolean b = (Boolean) expressionExecutor.execute(exprNode);
        if (b) {
            statementExecutor.execute(thenStmtNode);
        } else if (elseStmtNode != null) {
            statementExecutor.execute(elseStmtNode);
        }

        ++executionCount;   // count the IF statement itself
        return null;
    }
}
