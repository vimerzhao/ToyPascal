package wci.backend.interpreter.executors;


import wci.backend.interpreter.Executor;
import wci.intermediate.ICodeNode;

import java.util.ArrayList;

public class CompoundExecutor extends StatementExecutor {
    /**
     * Constructor.
     *
     * @param parent executor.
     */
    public CompoundExecutor(Executor parent) {
        super(parent);
    }

    /**
     * Execute a compound statement.
     * @param node the root node of the compound statement.
     * @return null.
     */
    @Override
    public Object execute(ICodeNode node) {
        // Loop over the children of the COMPOUND node and execute each child.
        StatementExecutor statementExecutor = new StatementExecutor(this);
        ArrayList<ICodeNode> children = node.getChildren();
        for (ICodeNode child : children) {
            statementExecutor.execute(child);
        }

        return null;
    }
}
