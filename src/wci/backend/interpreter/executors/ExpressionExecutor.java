package wci.backend.interpreter.executors;

import wci.backend.BackendFactory;
import wci.backend.interpreter.ActivationRecord;
import wci.backend.interpreter.Cell;
import wci.backend.interpreter.Executor;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.ICodeNodeTypeImpl;
import wci.intermediate.symtabimpl.Predefined;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import static wci.backend.interpreter.RuntimeErrorCode.DIVISION_BY_ZERO;
import static wci.backend.interpreter.RuntimeErrorCode.UNINITIALIZED_VALUE;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.ID;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.VALUE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.symtabimpl.RoutineCodeImpl.DECLARED;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_CODE;
import static wci.intermediate.typeimpl.TypeFormImpl.SUBRANGE;
import static wci.intermediate.typeimpl.TypeKeyImpl.ARRAY_ELEMENT_TYPE;
import static wci.intermediate.typeimpl.TypeKeyImpl.ARRAY_INDEX_TYPE;
import static wci.intermediate.typeimpl.TypeKeyImpl.SUBRANGE_MIN_VALUE;

/**
 * <h1>ExpressionExecutor</h1>
 *
 * <p>Executor an expression.</p>
 */
public class ExpressionExecutor extends StatementExecutor {
    /**
     * Constructor.
     *
     * @param parent executor.
     */
    public ExpressionExecutor(Executor parent) {
        super(parent);
    }

    /**
     * Execute an expression.
     * @param node the root intermediate code node of the compound statement.
     * @return the computed value of the expression.
     */
    @Override
    public Object execute(ICodeNode node) {
        ICodeNodeTypeImpl nodeType = (ICodeNodeTypeImpl) node.getType();
        switch (nodeType) {
            case VARIABLE: {
                return executeValue(node);
            }
            case INTEGER_CONSTANT: {
                TypeSpec type = node.getTypeSpec();
                Integer value = (Integer) node.getAttribute(VALUE);

                return type == Predefined.booleanType ? value == 1 : value;
            }
            case REAL_CONSTANT: {
                // Return the float value
                return (Float) node.getAttribute(VALUE);
            }
            case STRING_CONSTANT: {
                // Return the string value.
                return (String)node.getAttribute(VALUE);
            }
            case NEGATE: {
                // Return the NEGATE node's expression node child.
                ArrayList<ICodeNode> children = node.getChildren();
                ICodeNode expressionNode = children.get(0);

                // Execute the expression and return the negative of its value.
                Object value = execute(expressionNode);
                if (value instanceof Integer) {
                    return -((Integer) value);
                } else {
                    return -((Float) value);
                }
            }
            case NOT: {
                // Get the NOT node's expression node child.
                ArrayList<ICodeNode> children = node.getChildren();
                ICodeNode expressionNode = children.get(0);

                // Execute the expression and return the negative of its value.
                boolean value = (Boolean) execute(expressionNode);
                return !value;
            }
            case CALL: {
                SymTabEntry functionId = (SymTabEntry) node.getAttribute(ID);
                RoutineCode routineCode = (RoutineCode) functionId.getAttribute(ROUTINE_CODE);
                CallExecutor callExecutor = new CallExecutor(this);
                Object value = callExecutor.execute(node);

                if (routineCode == DECLARED) {
                    String functionName = functionId.getName();
                    int nestingLevel = functionId.getSymTab().getNestingLevel();
                    ActivationRecord ar = runtimeStack.getTopmost(nestingLevel);
                    Cell functionValueCell = ar.getCell(functionName);
                    value = functionValueCell.getValue();

                    sendFetchMessage(node, functionId.getName(), value);
                }
                return value;
            }
            default: {
                // Must be a binary operator.
                return executeBinaryOperator(node, nodeType);
            }
        }
    }

    // Set of arithmetic operator node types.
    private static final EnumSet<ICodeNodeTypeImpl> ARITH_OP =
            EnumSet.of(ADD, SUBTRACT, MULTIPLY, FLOAT_DIVIDE, INTEGER_DIVIDE, MOD);

    /**
     * Execute a binary operator.
     * @param node the root node of the expression.
     * @param nodeType the node type.
     * @return the computed value of the expression.
     */
    private Object executeBinaryOperator(ICodeNode node, ICodeNodeTypeImpl nodeType) {
        // Get the two operand children of the operator node.
        ArrayList<ICodeNode> children = node.getChildren();
        ICodeNode operandNode1 = children.get(0);
        ICodeNode operandNode2 = children.get(1);

        // Operands.
        Object operand1 = execute(operandNode1);
        Object operand2 = execute(operandNode2);

        boolean integerMode = false;
        boolean characterMode =false;
        boolean stringMode = false;

        if ((operand1 instanceof Integer) && (operand2 instanceof Integer)) {
             integerMode = true;
         } else if ( ( (operand1 instanceof Character)
                || ( (operand1 instanceof String)
                && (((String) operand1).length() == 1) ))
                && ( (operand2 instanceof Character)
                || ( (operand2 instanceof String)
                && (((String) operand2).length() == 1)))) {
             characterMode = true;
         } else if ((operand1 instanceof String) && (operand2 instanceof String)) {
             stringMode = true;
         }


        // Arithmetic operators
        if (ARITH_OP.contains(nodeType)) {
            if (integerMode) {
                int value1 = (Integer) operand1;
                int value2 = (Integer) operand2;

                // Integer operations.
                switch (nodeType) {
                    case ADD: {
                        return value1 + value2;
                    }
                    case SUBTRACT: {
                        return value1 - value2;
                    }
                    case MULTIPLY: {
                        return value1 * value2;
                    }
                    case FLOAT_DIVIDE: {
                        // Check for division by zero.
                        if (value2 != 0) {
                            return ((float) value1) / ((float) value2);
                        } else {
                            errorHandler.flag(node, DIVISION_BY_ZERO, this);
                            return 0;
                        }
                    }
                    case INTEGER_DIVIDE: {
                        // Check for division by zero.
                        if (value2 != 0) {
                            return value1 / value2;
                        } else {
                            errorHandler.flag(node, DIVISION_BY_ZERO, this);
                            return 0;
                        }
                    }
                    case MOD: {
                        // Check for division by zero.
                        if (value2 != 0) {
                            return value1 % value2;
                        } else {
                            errorHandler.flag(node, DIVISION_BY_ZERO, this);
                            return 0;
                        }
                    }
                }
            } else {
                float value1 = operand1 instanceof  Integer
                                ? (Integer) operand1
                                : (Float) operand1;
                float value2 = operand2 instanceof  Integer
                        ? (Integer) operand2
                        : (Float) operand2;
                switch (nodeType) {
                    case ADD: {
                        return value1 + value2;
                    }
                    case SUBTRACT: {
                        return value1 - value2;
                    }
                    case MULTIPLY: {
                        return value1 * value2;
                    }
                    case FLOAT_DIVIDE: {
                        // Check for division by zero.
                        if (value2 != 0.0f) {
                            return value1 / value2;
                        } else {
                            errorHandler.flag(node, DIVISION_BY_ZERO, this);
                            return 0.0f;
                        }
                     }
                }
            }
        } else if ((nodeType == AND) || (nodeType == OR)) {
            boolean value1 = (Boolean) operand1;
            boolean value2 = (Boolean) operand2;

            switch (nodeType) {
                case AND: {
                    return value1 && value2;
                }
                case OR: {
                    return value1 || value2;
                }
            }
        } else if (integerMode) {
            // Relational operators between Integers
            int value1 = (Integer) operand1;
            int value2 = (Integer) operand2;

            // Integer operands
            switch (nodeType) {
                case EQ: {
                    return value1 == value2;
                }
                case NE: {
                    return value1 != value2;
                }
                case LT: {
                    return value1 < value2;
                }
                case LE: {
                    return value1 <= value2;
                }
                case GT: {
                    return value1 > value2;
                }
                case GE: {
                    return value1 >= value2;
                }
            }
        } else if (characterMode) {
            int value1 = operand1 instanceof Character ? (Character) operand1 : ((String) operand1).charAt(0);
            int value2 = operand2 instanceof Character ? (Character) operand2 : ((String) operand2).charAt(0);
            switch (nodeType) {
                 case EQ: return value1 == value2;
                 case NE: return value1 != value2;
                 case LT: return value1 <  value2;
                 case LE: return value1 <= value2;
                 case GT: return value1 >  value2;
                 case GE: return value1 >= value2;
            }
        } else if (stringMode) {
             String value1 = (String) operand1;
             String value2 = (String) operand2;

             // String operands.
             int comp = value1.compareTo(value2);
             switch (nodeType) {
                 case EQ: return comp == 0;
                 case NE: return comp != 0;
                 case LT: return comp <  0;
                 case LE: return comp <= 0;
                 case GT: return comp >  0;
                 case GE: return comp >= 0;
             }
        } else {
            // Relational operators for others
            float value1 = operand1 instanceof Integer
                    ? (Integer) operand1 : (Float) operand1;
            float value2 = operand2 instanceof Integer
                    ? (Integer) operand2 : (Float) operand2;

            // Integer operands
            switch (nodeType) {
                case EQ: {
                    return value1 == value2;
                }
                case NE: {
                    return value1 != value2;
                }
                case LT: {
                    return value1 < value2;
                }
                case LE: {
                    return value1 <= value2;
                }
                case GT: {
                    return value1 > value2;
                }
                case GE: {
                    return value1 >= value2;
                }
            }
        }
        return 0;   // should never get here
    }

    /**
     * Return a variable's value.
     * @param node
     * @return
     */
    private Object executeValue(ICodeNode node) {
        SymTabEntry variableId = (SymTabEntry) node.getAttribute(ID);
        String variableName = variableId.getName();
        TypeSpec variableType = variableId.getTypeSpec();

        Cell variableCell = executeVariable(node);
        Object value = variableCell.getValue();

        if (value != null) {
            value = toJava(variableType, value);
        } else {
            errorHandler.flag(node, UNINITIALIZED_VALUE, this);
            value = BackendFactory.defaultValue(variableType);
            variableCell.setValue(value);
        }
        sendFetchMessage(node, variableName, value);
        return value;
    }

    /**
     * Execute a variable and return the reference to its cell.
     * @param node
     * @return
     */
    public Cell executeVariable(ICodeNode node) {
        SymTabEntry variableId = (SymTabEntry) node.getAttribute(ID);
        String variableName = variableId.getName();
        TypeSpec variableType = variableId.getTypeSpec();
        int nestingLevel = variableId.getSymTab().getNestingLevel();

        ActivationRecord ar = runtimeStack.getTopmost(nestingLevel);
        Cell variableCell = ar.getCell(variableName);

        ArrayList<ICodeNode> modifiers = node.getChildren();

        if (variableCell.getValue() instanceof Cell) {
            variableCell = (Cell) variableCell.getValue();
        }

        for (ICodeNode modifier : modifiers) {
            ICodeNodeType nodeType = modifier.getType();

            if (nodeType == SUBSCRIPTS) {
                ArrayList<ICodeNode> subscripts = modifier.getChildren();

                for (ICodeNode subscript : subscripts) {
                    TypeSpec indextType = (TypeSpec) variableType.getAttribute(ARRAY_INDEX_TYPE);
                    int minIndex = indextType.getForm() == SUBRANGE ? (Integer) indextType.getAttribute(SUBRANGE_MIN_VALUE) : 0;
                    int value = (Integer) execute(subscript);
                    value = (Integer) checkRange(node, indextType, value);

                    int index = value - minIndex;
                    variableCell = ((Cell[]) variableCell.getValue())[index];
                    variableType = (TypeSpec) variableType.getAttribute(ARRAY_ELEMENT_TYPE);

                }
            } else if (nodeType == FIELD) {
                SymTabEntry fieldId = (SymTabEntry) modifier.getAttribute(ID);
                String fieldName = fieldId.getName();

                HashMap<String, Cell> map = (HashMap<String, Cell>) variableCell.getValue();
                variableCell = map.get(fieldName);
                variableType = fieldId.getTypeSpec();
            }
        }
        return variableCell;
    }
}
