package wci.intermediate;

/**
 * <h1>SymTabStack</h1>
 * <p>The interface for the symbol table stack.</p>
 */
public interface SymTabStack {
    /**
     * Getter.
     * @return the current nesting level.
     */
    public int getCurrentNestingLevel();

    /**
     * Return the local symbol table which is at the top of the stack.
     * @return the local symbol table.
     */
    public SymTab getLocalSymTab();

    /**
     * Create and enter a new entry into the local symbol table.
     * @param name the name of the entry.
     * @return the new entry.
     */
    public SymTabEntry enterLocal(String name);

    /**
     * Look up an existing symbol table entry in the local symbol table.
     * @param name the name of the entry.
     * @return the entry, or null if it does not exist.
     */
    public SymTabEntry lookupLocal(String name);

    /**
     * Look up an existing symbol table entry throughout the stack.
     * @param name the name of the entry.
     * @return the entry, or null if it does not exist.
     */
    public SymTabEntry lookup(String name);

    // addimpl 2017-3-4

    /**
     * Setter
     * @param entry the symbol table entry for the main program identifier.
     */
    public void setProgramId(SymTabEntry entry);

    /**
     * Getter.
     * @return the symbol table entry for the main program identifier.
     */
    public SymTabEntry getProgramId();

    /**
     * Push a new symbol table onto the stack.
     * @return the pushed symbol table.
     */
    public SymTab push();

    /**
     * Push a symbol table onto the stack.
     * @param symTab the symbol table to push.
     * @return the pushed symbol table.
     */
    public SymTab push(SymTab symTab);

    /**
     * Pop a symbol table off the stack.
     * @return the popped symbol table.
     */
    public SymTab pop();
}
