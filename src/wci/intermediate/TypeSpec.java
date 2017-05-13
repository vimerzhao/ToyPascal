package wci.intermediate;

/**
 * TypeSpec
 * <p>
 * The interface for a type specification.
 */
public interface TypeSpec {
    /**
     * Getter
     *
     * @return the type form.
     */
    public TypeForm getForm();

    /**
     * Getter.
     *
     * @return the type identifier (symbol table entry).
     */
    public SymTabEntry getIdentifier();

    /**
     * Setter.
     *
     * @param identifier the type identifier (symbol table entry).
     */
    public void setIdentifier(SymTabEntry identifier);

    /**
     * Set an attribute of the specification.
     *
     * @param key   the attribute key.
     * @param value the attribute value.
     */
    public void setAttribute(TypeKey key, Object value);

    /**
     * Get the value of an attribute of the specification.
     *
     * @param key the attribute key.
     * @return the attribute value.
     */
    public Object getAttribute(TypeKey key);

    /**
     * @return true if this is a Pascal string type.
     */
    public boolean isPascalString();

    /**
     * @return the base type of this type.
     */
    public TypeSpec baseType();
}
