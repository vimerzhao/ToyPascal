package wci.intermediate.typeimpl;

import wci.intermediate.TypeForm;

/**
 * <h1>TypeFormImpl</h1>
 *
 * <p>Type forms for a Pascal type specification.</p>
 */
public enum TypeFormImpl implements TypeForm {
    SCALAR,
    ENUMERATION,
    SUBRANGE,
    ARRAY,
    RECORD;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
