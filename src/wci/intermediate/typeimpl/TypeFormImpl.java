package wci.intermediate.typeimpl;

import wci.intermediate.TypeForm;

/**
 * TypeFormImpl
 *
 * Type forms for a Pascal type specification.
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
