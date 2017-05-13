package wci.backend.interpreter.memoryimpl;

import wci.backend.interpreter.Cell;
import wci.backend.interpreter.MemoryFactory;
import wci.backend.interpreter.MemoryMap;
import wci.intermediate.*;
import wci.intermediate.typeimpl.TypeFormImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;

public class MemoryMapImpl extends HashMap<String, Cell> implements MemoryMap {
    /**
     * Create a memory map and allocate its memory cells based on the entries in a symbol table.
     *
     * @param symTab
     */
    public MemoryMapImpl(SymTab symTab) {
        ArrayList<SymTabEntry> entries = symTab.sortedEntries();

        for (SymTabEntry entry : entries) {
            Definition defn = entry.getDefinition();

            if ((defn == VARIABLE) || (defn == FUNCTION) || (defn == VALUE_PARM) || (defn == FIELD)) {
                String name = entry.getName();
                TypeSpec type = entry.getTypeSpec();
                put(name, MemoryFactory.createCell(allocateCellValue(type)));
            } else if (defn == VAR_PARM) {
                String name = entry.getName();
                put(name, MemoryFactory.createCell(null));
            }
        }
    }

    @Override
    public Cell getCell(String name) {
        return get(name);
    }

    @Override
    public ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList<>();
        Set<String> names = keySet();
        Iterator<String> it = names.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    private Object allocateCellValue(TypeSpec type) {
        TypeForm form = type.getForm();

        switch ((TypeFormImpl) form) {
            case ARRAY: {
                return allocateArrayCells(type);
            }
            case RECORD: {
                return allocateRecordMap(type);
            }
            default: {
                return null;    // uninitialized scalar value.
            }
        }
    }

    private Object[] allocateArrayCells(TypeSpec type) {
        int elmtCount = (Integer) type.getAttribute(ARRAY_ELEMENT_COUNT);
        TypeSpec elmtType = (TypeSpec) type.getAttribute(ARRAY_ELEMENT_TYPE);

        Cell[] allocation = new Cell[elmtCount];
        for (int i = 0; i < elmtCount; ++i) {
            allocation[i] = MemoryFactory.createCell(allocateCellValue(elmtType));
        }
        return allocation;
    }

    private MemoryMap allocateRecordMap(TypeSpec type) {
        SymTab symTab = (SymTab) type.getAttribute(RECORD_SYMTAB);
        MemoryMap memoryMap = MemoryFactory.createMemoryMap(symTab);

        return memoryMap;
    }
}
