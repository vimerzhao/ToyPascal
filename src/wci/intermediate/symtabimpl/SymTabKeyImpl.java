package wci.intermediate.symtabimpl;

import wci.intermediate.SymTabKey;

public enum SymTabKeyImpl implements SymTabKey {
    // Constant.
    CONSTANT_VALUE,

    // Procedure or function.
    ROUTINE_CODE, ROUTINE_SYMTAB, ROUTINE_ICODE,
    ROUTINE_PARMS, ROUTINE_ROUTINES,

    // Variable or record field value.
    DATA_VALUE
}
