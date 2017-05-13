package wci.intermediate.symtabimpl;

import wci.intermediate.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SymTabImpl
 *
 * An implementation of a symbol table entry.
 */
public class SymTabEntryImpl extends HashMap<SymTabKey, Object> implements SymTabEntry {
    private String name;                    // entry name
    private SymTab symTab;                  // parent symbol table
    private Definition definition;           // how the identifier is defined
    private TypeSpec typeSpec;               // type specification
    private ArrayList<Integer> lineNumbers; // source line numbers

    /**
     * Constructor.
     *
     * @param name   the name of the entry.
     * @param symTab the symbol table that contains this entry.
     */
    public SymTabEntryImpl(String name, SymTab symTab) {
        this.name = name;
        this.symTab = symTab;
        this.lineNumbers = new ArrayList<>();
    }

    /**
     * Getter.
     *
     * @return the name of the entry.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Getter.
     *
     * @return the symbol table that contain ths entry.
     */
    @Override
    public SymTab getSymTab() {
        return symTab;
    }

    /**
     * Getter.
     *
     * @return the type specification.
     */
    public TypeSpec getTypeSpec() {
        return typeSpec;
    }

    /**
     * Setter.
     *
     * @param typeSpec the type specification to set.
     */
    public void setTypeSpec(TypeSpec typeSpec) {
        this.typeSpec = typeSpec;
    }

    /**
     * Getter.
     *
     * @return the definition.
     */
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Setter.
     *
     * @param definition the definition to set.
     */
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    /**
     * Append a source line number to the entry.
     *
     * @param lineNumber the line number to append.
     */
    @Override
    public void appendLineNumber(int lineNumber) {
        lineNumbers.add(lineNumber);
    }

    @Override
    public ArrayList<Integer> getLineNumbers() {
        return lineNumbers;
    }

    /**
     * Set an attribute of the entry.
     *
     * @param key   the attribute key.
     * @param value thr attribute value.
     */
    @Override
    public void setAttribute(SymTabKey key, Object value) {
        put(key, value);
    }

    /**
     * Get the value of an attribute of the entry.
     *
     * @param key the attribute key.
     * @return the attribute value.
     */
    @Override
    public Object getAttribute(SymTabKey key) {
        return get(key);
    }

}
