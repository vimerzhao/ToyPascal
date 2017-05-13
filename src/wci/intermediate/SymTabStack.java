package wci.intermediate;

/**
 * SymTabStack
 * <p>
 * The interface for the symbol table stack.
 */
public interface SymTabStack {
    /**
     * Getter.
     *
     * @return the current nesting level.
     */
    int getCurrentNestingLevel();

    /**
     * Return the local symbol table which is at the top of the stack.
     *
     * @return the local symbol table.
     */
    SymTab getLocalSymTab();

    /**
     * Create and enter a new entry into the local symbol table.
     *
     * @param name the name of the entry.
     * @return the new entry.
     */
    SymTabEntry enterLocal(String name);

    /**
     * Look up an existing symbol table entry in the local symbol table.
     *
     * @param name the name of the entry.
     * @return the entry, or null if it does not exist.
     */
    SymTabEntry lookupLocal(String name);

    /**
     * Look up an existing symbol table entry throughout the stack.
     *
     * @param name the name of the entry.
     * @return the entry, or null if it does not exist.
     */
    SymTabEntry lookup(String name);

    // addimpl 2017-3-4

    /**
     * Getter.
     *
     * @return the symbol table entry for the main program identifier.
     */
    SymTabEntry getProgramId();

    /**
     * Setter
     *
     * @param entry the symbol table entry for the main program identifier.
     */
    void setProgramId(SymTabEntry entry);

    /**
     * Push a new symbol table onto the stack.
     *
     * @return the pushed symbol table.
     */
    SymTab push();

    /**
     * Push a symbol table onto the stack.
     *
     * @param symTab the symbol table to push.
     * @return the pushed symbol table.
     */
    SymTab push(SymTab symTab);

    /**
     * Pop a symbol table off the stack.
     *
     * @return the popped symbol table.
     */
    SymTab pop();
}
