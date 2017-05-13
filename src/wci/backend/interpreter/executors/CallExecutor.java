package wci.backend.interpreter.executors;

import wci.backend.interpreter.Executor;
import wci.intermediate.ICodeNode;
import wci.intermediate.RoutineCode;
import wci.intermediate.SymTabEntry;

import static wci.intermediate.icodeimpl.ICodeKeyImpl.ID;
import static wci.intermediate.symtabimpl.RoutineCodeImpl.DECLARED;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_CODE;

public class CallExecutor extends StatementExecutor {
    /**
     * Constructor.
     *
     * @param parent executor.
     */
    public CallExecutor(Executor parent) {
        super(parent);
    }

    /**
     * Execute procedure or function call statement.
     *
     * @param node the root node of the statement.
     * @return null.
     */
    public Object execute(ICodeNode node) {
        SymTabEntry routineId = (SymTabEntry) node.getAttribute(ID);
        RoutineCode routineCode = (RoutineCode) routineId.getAttribute(ROUTINE_CODE);
        CallExecutor callExecutor = routineCode == DECLARED
                ? new CallDeclaredExecutor(this)
                : new CallStandardExecutor(this);
        ++executionCount;
        return callExecutor.execute(node);
    }
}
