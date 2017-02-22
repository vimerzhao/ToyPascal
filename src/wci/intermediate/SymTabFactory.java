package wci.intermediate;

import wci.intermediate.symtabimpl.SymTabEntryImpl;
import wci.intermediate.symtabimpl.SymTabImpl;
import wci.intermediate.symtabimpl.SymTabStackImpl;

/**
 * <h1>SymTabFactory</h1>
 *
 * <p>A factory for create objects that implement the symbol table.</p>
 */
public class SymTabFactory {
    /**
     * Create and return a symbol table stack implementation.
     * @return the symbol table implementation
     */
    public static SymTabStack createSymTabStack() {
        return new SymTabStackImpl();
    }

    /**
     * Create and return a symbol table implementation.
     * @param nestingLevel the nesting level.
     * @return the symbol table implementation.
     */
    public static SymTab createSymTab(int nestingLevel) {
        return new SymTabImpl(nestingLevel);
    }

    public static SymTabEntry createSymTabEntry(String name, SymTab symTab) {
        return new SymTabEntryImpl(name, symTab);
    }
}
