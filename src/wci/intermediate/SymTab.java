package wci.intermediate;

import java.util.ArrayList;

/**
 * SymTab
 * <p>
 * The framework interface that represents the symbol table.
 */
public interface SymTab {
    /**
     * Getter.
     *
     * @return the scope nesting level of this entry.
     */
    int getNestingLevel();

    /**
     * Create and enter a new entry into the symbol table.
     *
     * @param name the name of the entry.
     * @return the new entry.
     */
    SymTabEntry enter(String name);

    /**
     * Look up an existing symbol table entry.
     *
     * @param name the name of the entry.
     * @return the entry, or null if it does not exist.
     */
    SymTabEntry lookup(String name);

    /**
     * @return a list of symbol table entries sorted by name.
     */
    ArrayList<SymTabEntry> sortedEntries();
}
