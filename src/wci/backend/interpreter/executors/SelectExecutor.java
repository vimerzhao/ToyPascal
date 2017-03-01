package wci.backend.interpreter.executors;

import wci.backend.interpreter.Executor;
import wci.intermediate.ICodeNode;

import java.util.ArrayList;

import static wci.intermediate.icodeimpl.ICodeKeyImpl.VALUE;

public class SelectExecutor extends StatementExecutor {
    /**
     * Constructor.
     *
     * @param parent executor.
     */
    public SelectExecutor(Executor parent) {
        super(parent);
    }

    /**
     * Execute SELECT statement.
     * @param node the root node of the statement.
     * @return null.
     */
    @Override
    public Object execute(ICodeNode node) {
        // Get the SELECT node's children.
        ArrayList<ICodeNode> selectChildren = node.getChildren();
        ICodeNode exprNode = selectChildren.get(0);

        // Evaluate the SELECT expression.
        ExpressionExecutor expressionExecutor = new ExpressionExecutor(this);
        Object selectValue = expressionExecutor.execute(exprNode);

        // Attempt to select a SELECT_BRANCH.
        ICodeNode selectBranchNode = searchBranches(selectValue, selectChildren);

        // If there was a selection, execute the SELECT_BRANCH's statement.
        if (selectBranchNode != null) {
            ICodeNode stmtNode = selectBranchNode.getChildren().get(1);
            StatementExecutor statementExecutor = new StatementExecutor(this);
            statementExecutor.execute(stmtNode);
        }

        ++executionCount;   // count the SELECT statement itself
        return null;
    }

    /**
     * Search the SELECT_BRANCHes to find a match.
     * @param selectValue the value to match.
     * @param selectChildren the children of the SELECT node.
     * @return ICodeNode
     */
    private ICodeNode searchBranches(Object selectValue, ArrayList<ICodeNode> selectChildren) {
        // Loop over the SELECT_BRANCHes to find a match.
        for (int i = 1; i < selectChildren.size(); ++i) {
            ICodeNode branchNode = selectChildren.get(i);
            if (searchConstants(selectValue, branchNode)) {
                return branchNode;
            }
        }
        return null;
    }

    /**
     * Search the constant of a SELECT_BRANCH for a matching value.
     * @param selectValue the value to match.
     * @param branchNode the SELECT_BRANCH node.
     * @return boolean
     */
    private boolean searchConstants(Object selectValue, ICodeNode branchNode) {
        // Are the values integer or string?
        boolean integerMode = selectValue instanceof Integer;

        // Get the list of SELECT_CONSTANTS values.
        ICodeNode constantsNode = branchNode.getChildren().get(0);
        ArrayList<ICodeNode> constantsList = constantsNode.getChildren();

        // Search the list of constants.
        if (selectValue instanceof Integer) {
            for (ICodeNode constantNode : constantsList) {
                int constant = (Integer) constantNode.getAttribute(VALUE);

                if (((Integer) selectValue) == constant) {
                    return true;    // match
                }
            }
        } else {
            for (ICodeNode constantNode : constantsList) {
                String constant = (String) constantNode.getAttribute(VALUE);

                if (((String) selectValue).equals(constant)) {
                    return true;    // match
                }
            }
        }
        return false;   // no match
    }
}
