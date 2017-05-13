package wci.backend.interpreter.executors;

import wci.backend.interpreter.Executor;
import wci.intermediate.ICodeNode;
import wci.intermediate.icodeimpl.ICodeNodeTypeImpl;

import java.util.List;

import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.TEST;

public class LoopExecutor extends StatementExecutor {
    /**
     * Constructor.
     *
     * @param parent executor.
     */
    public LoopExecutor(Executor parent) {
        super(parent);
    }

    /**
     * Execute a loop statement.
     *
     * @param node the root node of the statement.
     * @return null
     */
    @Override
    public Object execute(ICodeNode node) {
        boolean exitLoop = false;
        ICodeNode exprNode = null;
        List<ICodeNode> loopChiledren = node.getChildren();

        ExpressionExecutor expressionExecutor = new ExpressionExecutor(this);
        StatementExecutor statementExecutor = new StatementExecutor(this);

        // Loop until the TEST expression value is true.
        while (!exitLoop) {
            ++executionCount;   // count the LOOP statement itself

            // Execute the children of the LOOP node.
            for (ICodeNode child : loopChiledren) {
                ICodeNodeTypeImpl childType = (ICodeNodeTypeImpl) child.getType();

                // TEST node:
                if (childType == TEST) {
                    if (exprNode == null) {
                        exprNode = child.getChildren().get(0);
                    }
                    exitLoop = (Boolean) expressionExecutor.execute(exprNode);
                } else {    // Statement node.
                    statementExecutor.execute(child);
                }

                // Exit if the TEST expression value is true.
                if (exitLoop) {
                    break;
                }
            }
        }

        return null;
    }
}
