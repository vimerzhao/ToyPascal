package wci.backend.interpreter.debuggerimpl;

import wci.backend.interpreter.Cell;
import wci.backend.interpreter.Debugger;
import wci.frontend.pascal.PascalTokenType;
import wci.intermediate.SymTab;
import wci.intermediate.SymTabEntry;
import wci.intermediate.TypeForm;
import wci.intermediate.TypeSpec;
import wci.intermediate.symtabimpl.Predefined;
import wci.intermediate.typeimpl.TypeFormImpl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.intermediate.typeimpl.TypeFormImpl.ENUMERATION;
import static wci.intermediate.typeimpl.TypeFormImpl.SUBRANGE;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;

/**
 * Memory cell and data type pair used by the debugger.
 */
public class CellTypePair {
    // Synchronization set for variable modifiers.
    protected static final EnumSet<PascalTokenType> MODIFIER_SET = EnumSet.of(LEFT_BRACKET, DOT);
    private Cell cell;          // memory cell
    private TypeSpec type;  // data type
    private Debugger debugger;  // parent debugger.

    /**
     * Constructor.
     *
     * @param type     the data type.
     * @param cell     the memory cell.
     * @param debugger the parent debugger.
     * @throws Exception if an error occurred.
     */
    protected CellTypePair(TypeSpec type, Cell cell, Debugger debugger) throws Exception {
        this.type = type;
        this.cell = cell;
        this.debugger = debugger;

        parseVariable();
    }

    public Cell getCell() {
        return cell;
    }

    public TypeSpec getType() {
        return type;
    }

    /**
     * Parse a variable in the command to obtain its memory cell.
     *
     * @throws Exception
     */
    protected void parseVariable() throws Exception {
        TypeForm form = type.getForm();
        Object value = cell.getValue();

        //Loop to process array subscripts and record field.
        while (MODIFIER_SET.contains(debugger.currentToken().getType())) {
            if (form == TypeFormImpl.ARRAY) {
                parseArrayVariable((Cell[]) value);
            } else if (form == TypeFormImpl.RECORD) {
                parseRecordVariable((HashMap) value);
            }
            value = cell.getValue();
            form = type.getForm();
        }
    }

    /**
     * Parse an array variable.
     *
     * @param array the array variable.
     * @throws Exception
     */
    private void parseArrayVariable(Cell[] array) throws Exception {
        debugger.nextToken();
        int index = debugger.getInteger("Integer index expected.");
        int minValue = 0;
        TypeSpec indexType = (TypeSpec) type.getAttribute(ARRAY_INDEX_TYPE);

        rangeCheck(index, indexType, "Index out of range.");
        type = (TypeSpec) type.getAttribute(ARRAY_ELEMENT_TYPE);
        if (indexType.getForm() == SUBRANGE) {
            minValue = (Integer) indexType.getAttribute(SUBRANGE_MIN_VALUE);
        }
        cell = array[index - minValue];
        if (debugger.currentToken().getType() == RIGHT_BRACKET) {
            debugger.nextToken();
        } else {
            throw new Exception("] expected.");
        }
    }

    /**
     * Parse a record variable.
     *
     * @param record the record variable.
     * @throws Exception
     */
    private void parseRecordVariable(HashMap record) throws Exception {
        debugger.nextToken();
        String fieldName = debugger.getWord("Field name expected.");
        if (record.containsKey(fieldName)) {
            cell = (Cell) record.get(fieldName);
        } else {
            throw new Exception("Invalid field name.");
        }
        SymTab symTab = (SymTab) type.getAttribute(RECORD_SYMTAB);
        SymTabEntry id = symTab.lookup(fieldName);
        type = id.getTypeSpec();
    }

    /**
     * Set the value of the cell.
     *
     * @param value the value.
     * @throws Exception
     */
    protected void setValue(Object value) throws Exception {
        if (((type.baseType() == Predefined.integerType)
                && (value instanceof Integer))
                || ((type == Predefined.realType)
                && (value instanceof Float))
                || ((type == Predefined.booleanType)
                && (value instanceof Boolean))
                || ((type == Predefined.charType)
                && (value instanceof Character))) {
            if (type.baseType() == Predefined.integerType) {
                rangeCheck((Integer) value, type, "Value out of range.");
            }

            cell.setValue(value);
        } else {
            throw new Exception("Type mismatch.");
        }
    }

    /**
     * Do a range check on an integer value.
     *
     * @param value        the value.
     * @param type         the data type.
     * @param errorMessage
     * @throws Exception
     */
    private void rangeCheck(int value, TypeSpec type, String errorMessage) throws Exception {
        TypeForm form = type.getForm();
        Integer minValue = null;
        Integer maxValue = null;

        if (form == SUBRANGE) {
            minValue = (Integer) type.getAttribute(SUBRANGE_MIN_VALUE);
            maxValue = (Integer) type.getAttribute(SUBRANGE_MAX_VALUE);
        } else if (form == ENUMERATION) {
            ArrayList<SymTabEntry> constants = (ArrayList<SymTabEntry>) type.getAttribute(ENUMERATION_CONSTANTS);
            minValue = 0;
            maxValue = constants.size() - 1;
        }
        if ((minValue != null) && ((value < minValue) || (value > maxValue))) {
            throw new Exception(errorMessage);
        }
    }

}
