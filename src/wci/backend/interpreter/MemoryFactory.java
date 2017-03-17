package wci.backend.interpreter;

import wci.backend.interpreter.memoryimpl.*;
import wci.intermediate.SymTab;
import wci.intermediate.SymTabEntry;

/**
 * Create runtime components.
 */
public class MemoryFactory {
    public static RuntimeStack createRuntimeStack() {
        return new RuntimeStackImpl();
    }

    public static RuntimeDisplay createRuntimeDisplay() {
        return new RuntimeDisplayImpl();
    }

    public static ActivationRecord createActivationRecord(SymTabEntry routineId) {
        return new ActivationRecordImpl(routineId);
    }

    public static MemoryMap createMemoryMap(SymTab symTab) {
        return new MemoryMapImpl(symTab);
    }

    public static Cell createCell(Object value) {
        return new CellImpl(value);
    }
}
