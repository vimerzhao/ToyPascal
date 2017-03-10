package wci.intermediate.typeimpl;

import wci.intermediate.TypeKey;

/**
 * <h1>TypeKeyImpl</h1>
 *
 * <p>Attribute key for a Pascal type specification.</p>
 */
public enum TypekeyImpl implements TypeKey {
    // Enumeration
    ENUMERATION_CONSTANTS,

    // Subrange
    SUBRANGE_BASE_TYPE,
    SUBRANGE_MIN_VALUE,
    SUBRANGE_MAX_VALUE,

    // Array
    ARRAY_INDEX_TYPE,
    ARRAY_ELEMENT_TYPE,
    ARRAY_ELEMENT_COUNT,

    // Record
    RECORD_SYMTAB
}
