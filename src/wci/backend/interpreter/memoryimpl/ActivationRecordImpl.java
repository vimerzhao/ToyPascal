package wci.backend.interpreter.memoryimpl;

import wci.backend.interpreter.ActivationRecord;
import wci.backend.interpreter.Cell;
import wci.backend.interpreter.MemoryFactory;
import wci.backend.interpreter.MemoryMap;
import wci.intermediate.SymTab;
import wci.intermediate.SymTabEntry;

import java.util.ArrayList;

import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_SYMTAB;

public class ActivationRecordImpl implements ActivationRecord {
    private SymTabEntry routineId;  // symbol table entry of the routine's name
    private ActivationRecord link;  // dynamic link to the previous record.
    private int nestingLevel;       // scope nesting level of this record.
    private MemoryMap memoryMap;    // memory map of this stack record.

    public ActivationRecordImpl(SymTabEntry routineId) {
        SymTab symTab = (SymTab) routineId.getAttribute(ROUTINE_SYMTAB);

        this.routineId = routineId;
        this.nestingLevel = symTab.getNestingLevel();
        this.memoryMap = MemoryFactory.createMemoryMap(symTab);
    }

    @Override
    public SymTabEntry getRoutineId() {
        return routineId;
    }

    @Override
    public Cell getCell(String name) {
        return memoryMap.getCell(name);
    }

    @Override
    public ArrayList<String> getAllNames() {
        return memoryMap.getAllNames();
    }

    @Override
    public int getNestingLevel() {
        return nestingLevel;
    }

    @Override
    public ActivationRecord linkedTo() {
        return link;
    }

    @Override
    public ActivationRecord makeLinkTo(ActivationRecord ar) {
        link = ar;
        return this;
    }
}
