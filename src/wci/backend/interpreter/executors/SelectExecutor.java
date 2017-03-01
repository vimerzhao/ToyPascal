package wci.backend.interpreter.executors;

import wci.backend.interpreter.Executor;
import wci.intermediate.ICode;
import wci.intermediate.ICodeNode;

import java.util.ArrayList;
import java.util.HashMap;

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

    // Optimized version

    // Jump table cache: entry key is a SELECT node,entry value is the jump table.
    // Jump table: entry key is a selection value, entry key is the branch statement.
    private static HashMap<ICodeNode, HashMap<Object, ICodeNode>> jumpCache = new HashMap<>();

    /**
     * Execute SELECT statement.
     * @param node the root node of the statement.
     * @return null.
     */
    @Override
    public Object execute(ICodeNode node) {
        // Is there already an entry for this SELECT node in the
        // jump table cache?If not,create a jump table entry.
        HashMap<Object, ICodeNode> jumpTable = jumpCache.get(node);
        if (jumpTable == null) {
            jumpTable = createJumpTable(node);
            jumpCache.put(node, jumpTable);
        }

        // Get the SELECT node's children.
        ArrayList<ICodeNode> selectChildren = node.getChildren();
        ICodeNode exprNode = selectChildren.get(0);

        // Evaluate the SELECT expression.
        ExpressionExecutor expressionExecutor = new ExpressionExecutor(this);
        Object selectValue = expressionExecutor.execute(exprNode);

        // If there is a selection,execute the SELECT_BRANCH's statement.
        ICodeNode statementNode = jumpTable.get(selectValue);
        if (statementNode != null) {
            StatementExecutor statementExecutor = new StatementExecutor(this);
            statementExecutor.execute(statementNode);
        }

        ++executionCount;   // count the SELECT statement itself
        return null;
    }

    /**
     * Create a jump table for a SELECT node.
     * @param node the SELECT node.
     * @return the jump table.
     */
    private HashMap<Object, ICodeNode> createJumpTable(ICodeNode node) {
        HashMap<Object, ICodeNode> jumpTable = new HashMap<>();

        // Loop over children that are SELECT_BRANCH nodes.
        ArrayList<ICodeNode> selectChildren = node.getChildren();
        for (int i = 1; i < selectChildren.size(); ++i) {
            ICodeNode branchNode = selectChildren.get(i);
            ICodeNode constantsNode = branchNode.getChildren().get(0);
            ICodeNode statementNode = branchNode.getChildren().get(1);

            // Loop over the constants children of the branch's CONSTANTS_NODE.
            ArrayList<ICodeNode> constantsList = constantsNode.getChildren();
            for (ICodeNode constantNode : constantsList) {
                // Create a jump table entry.
                Object value = constantNode.getAttribute(VALUE);
                jumpTable.put(value, statementNode);
            }
        }
        return jumpTable;
    }
}

//    delete following code for a linear search to low,
//    this is an optimization(first time) for the compiler/interpreter
//    2017-3-1

//    /**
//     * Execute SELECT statement.
//     * @param node the root node of the statement.
//     * @return null.
//     */
//    @Override
//    public Object execute(ICodeNode node) {
//        // Get the SELECT node's children.
//        ArrayList<ICodeNode> selectChildren = node.getChildren();
//        ICodeNode exprNode = selectChildren.get(0);
//
//        // Evaluate the SELECT expression.
//        ExpressionExecutor expressionExecutor = new ExpressionExecutor(this);
//        Object selectValue = expressionExecutor.execute(exprNode);
//
//        // Attempt to select a SELECT_BRANCH.
//        ICodeNode selectBranchNode = searchBranches(selectValue, selectChildren);
//
//        // If there was a selection, execute the SELECT_BRANCH's statement.
//        if (selectBranchNode != null) {
//            ICodeNode stmtNode = selectBranchNode.getChildren().get(1);
//            StatementExecutor statementExecutor = new StatementExecutor(this);
//            statementExecutor.execute(stmtNode);
//        }
//
//        ++executionCount;   // count the SELECT statement itself
//        return null;
//    }
//
//    /**
//     * Search the SELECT_BRANCHes to find a match.
//     * @param selectValue the value to match.
//     * @param selectChildren the children of the SELECT node.
//     * @return ICodeNode
//     */
//    private ICodeNode searchBranches(Object selectValue, ArrayList<ICodeNode> selectChildren) {
//        // Loop over the SELECT_BRANCHes to find a match.
//        for (int i = 1; i < selectChildren.size(); ++i) {
//            ICodeNode branchNode = selectChildren.get(i);
//            if (searchConstants(selectValue, branchNode)) {
//                return branchNode;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Search the constant of a SELECT_BRANCH for a matching value.
//     * @param selectValue the value to match.
//     * @param branchNode the SELECT_BRANCH node.
//     * @return boolean
//     */
//    private boolean searchConstants(Object selectValue, ICodeNode branchNode) {
//        // Are the values integer or string?
//        boolean integerMode = selectValue instanceof Integer;
//
//        // Get the list of SELECT_CONSTANTS values.
//        ICodeNode constantsNode = branchNode.getChildren().get(0);
//        ArrayList<ICodeNode> constantsList = constantsNode.getChildren();
//
//        // Search the list of constants.
//        if (selectValue instanceof Integer) {
//            for (ICodeNode constantNode : constantsList) {
//                int constant = (Integer) constantNode.getAttribute(VALUE);
//
//                if (((Integer) selectValue) == constant) {
//                    return true;    // match
//                }
//            }
//        } else {
//            for (ICodeNode constantNode : constantsList) {
//                String constant = (String) constantNode.getAttribute(VALUE);
//
//                if (((String) selectValue).equals(constant)) {
//                    return true;    // match
//                }
//            }
//        }
//        return false;   // no match
//    }