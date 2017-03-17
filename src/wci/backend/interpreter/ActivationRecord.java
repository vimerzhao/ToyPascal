package wci.backend.interpreter;

import wci.intermediate.SymTabEntry;

import java.util.ArrayList;

public interface ActivationRecord {
    /**
     * @return the symbol table entry of the routine's name.
     */
    SymTabEntry getRoutineId();

    /**
     * Get the memory cell for the given name from the memory map.
     * @param name
     * @return the cell
     */
    Cell getCell(String name);

    /**
     * @return the list of all the name in the memory map.
     */
    ArrayList<String> getAllNames();

    int getNestingLevel();

    /**
     * @return the activation record to which this record is dynamically linked.
     */
    ActivationRecord linkedTo();

    /**
     * Make a dynamic link from this activation record to another one.
     * @param ar
     * @return
     */
    ActivationRecord makeLinkTo(ActivationRecord ar);
}
