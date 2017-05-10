package wci.backend.interpreter.executors;


import wci.backend.interpreter.Cell;
import wci.backend.interpreter.Executor;
import wci.backend.interpreter.MemoryFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.TypeSpec;
import wci.intermediate.icodeimpl.ICodeNodeTypeImpl;
import wci.intermediate.symtabimpl.Predefined;
import wci.message.Message;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static wci.backend.interpreter.RuntimeErrorCode.UNIMPLEMENTED_FEATURE;
import static wci.backend.interpreter.RuntimeErrorCode.UNINITIALIZED_VALUE;
import static wci.backend.interpreter.RuntimeErrorCode.VALUE_RANGE;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.LINE;
import static wci.intermediate.typeimpl.TypeFormImpl.SUBRANGE;
import static wci.intermediate.typeimpl.TypeKeyImpl.SUBRANGE_MAX_VALUE;
import static wci.intermediate.typeimpl.TypeKeyImpl.SUBRANGE_MIN_VALUE;
import static wci.message.MessageType.*;

public class StatementExecutor extends Executor{
    /**
     * Constructor.
     * @param parent executor.
     */
    public StatementExecutor(Executor parent) {
        super(parent);
    }

    /**
     * Execute a statement.
     * To be overridden by specialized statement executor subclass.
     * @param node the root node of the statement.
     * @return null
     */
    public Object execute(ICodeNode node) {
        ICodeNodeTypeImpl nodeType = (ICodeNodeTypeImpl) node.getType();

        // Send a message about the current source line
        sendSourceLineMessage(node);

        switch (nodeType) {
            case COMPOUND: {
                CompoundExecutor compoundExecutor = new CompoundExecutor(this);
                return compoundExecutor.execute(node);
            }
            case ASSIGN: {
                AssignmentExecutor assignmentExecutor = new AssignmentExecutor(this);
                return assignmentExecutor.execute(node);

            }
            case LOOP: {
                LoopExecutor loopExecutor = new LoopExecutor(this);
                return loopExecutor.execute(node);
            }
            case IF: {
                IfExecutor ifExecutor = new IfExecutor(this);
                return ifExecutor.execute(node);
            }
            case SELECT: {
                SelectExecutor selectExecutor = new SelectExecutor(this);
                return selectExecutor.execute(node);
            }
            case NO_OP: {
                return null;
            }
            case CALL: {
                CallExecutor callExecutor = new CallExecutor(this);
                return callExecutor.execute(node);
            }
            default: {
                errorHandler.flag(node, UNIMPLEMENTED_FEATURE, this);
                return null;
            }
        }
    }

    /**
     * Send a message about the current source line.
     * @param node the statement node.
     */
    private void sendSourceLineMessage(ICodeNode node) {
        Object lineNumber = node.getAttribute(LINE);

        // Send the SOURCE_LINE message.
        if (lineNumber != null) {
            sendMessage(new Message(SOURCE_LINE, lineNumber));
        }
    }

    /**
     * Convert a Java string to a Pascal string or character.
     * @param targetType the target type specification.
     * @param javaValue the java string.
     * @return the Pascal string or char.
     */
    protected Object toPascal(TypeSpec targetType, Object javaValue) {
        if (javaValue instanceof String) {
            String string = (String) javaValue;

            if (targetType == Predefined.charType) {
                return string.charAt(0);
            } else if (targetType.isPascalString()) {
                Cell[] charCell = new Cell[string.length()];

                for (int i = 0; i < string.length(); i++) {
                    charCell[i] = MemoryFactory.createCell(string.charAt(i));
                }
                return charCell;// Pascal string(array of char)
            } else {
                return javaValue;
            }
        } else {
            return javaValue;
        }
    }

    /**
     * Convert a Pascal string to a Java string.
     * @param targetType
     * @param pascalValue
     * @return
     */
    protected Object toJava(TypeSpec targetType, Object pascalValue) {
        if ((pascalValue instanceof Cell[]) && (((Cell[]) pascalValue)[0].getValue() instanceof  Character)) {
            Cell[] charCells = (Cell[]) pascalValue;
            StringBuilder string = new StringBuilder(charCells.length);

            for (Cell ref : charCells) {
                string.append((Character) ref.getValue());
            }

            return string.toString();
        } else {
            return pascalValue;
        }
    }

    protected Object copyOf(Object value, ICodeNode node) {
        Object copy = null;

        if (value instanceof Integer) {
         copy = new Integer((Integer) value);
        } else if (value instanceof Float) {
         copy = new Float((Float) value);
        } else if (value instanceof Character) {
         copy = new Character((Character) value);
        } else if (value instanceof Boolean) {
         copy = new Boolean((Boolean) value);
        } else if (value instanceof String) {
         copy = new String((String) value);
        } else if (value instanceof HashMap) {
         copy = copyRecord((HashMap<String, Object>) value, node);
        } else {
         copy = copyArray((Cell[]) value, node);
        }

        return copy;
    }

    /**
     * Return a copy of a Pascal record.
     * @param value
     * @param node
     * @return
     */
    private Object copyRecord(HashMap<String, Object> value, ICodeNode node) {
        HashMap<String, Object> copy = new HashMap<>();
        if (value != null) {
            Set<Map.Entry<String, Object>> entries = value.entrySet();
            Iterator<Map.Entry<String, Object>> iterator = entries.iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                String newKey = new String(entry.getKey());
                Cell valueCell = (Cell) entry.getValue();
                Object newValue = copyOf(valueCell.getValue(), node);

                copy.put(newKey, MemoryFactory.createCell(newValue));
            }
        } else {
            errorHandler.flag(node, UNINITIALIZED_VALUE, this);
        }
        return copy;
    }

    /**
     * Return a copy of a Pascal array.
     * @param valueCells
     * @param node
     * @return
     */
    private Cell[] copyArray(Cell[] valueCells, ICodeNode node) {
        int length;
        Cell[] copy;

        if (valueCells != null) {
            length = valueCells.length;
            copy = new Cell[length];

            for (int i = 0; i < length; i++) {
                Cell valueCell = (Cell) valueCells[i];
                Object newValue = copyOf(valueCell.getValue(), node);
                copy[i] = MemoryFactory.createCell(newValue);
            }
        } else {
            errorHandler.flag(node, UNINITIALIZED_VALUE, this);
            copy = new Cell[1];
        }
        return copy;
    }

    protected Object checkRange(ICodeNode node, TypeSpec type, Object value) {
        if (type.getForm() == SUBRANGE) {
            int minValue = (Integer) type.getAttribute(SUBRANGE_MIN_VALUE);
            int maxValue = (Integer) type.getAttribute(SUBRANGE_MAX_VALUE);
            if (((Integer) value) < minValue) {
                errorHandler.flag(node, VALUE_RANGE, this);
                return minValue;
            } else if (((Integer) value) > maxValue) {
                errorHandler.flag(node, VALUE_RANGE, this);
                return maxValue;
            } else {
                return value;
            }
        } else {
            return value;
        }
    }

    /**
     * Send a message about an assignment operation.
     * @param node
     * @param variableName
     * @param value
     */
    protected void sendAssignMessage(ICodeNode node, String variableName, Object value) {
        Object lineNumber = getLineNumber(node);

        if (lineNumber != null) {
            sendMessage(new Message(ASSIGN, new Object[]{lineNumber, variableName, value}));
        }
    }

    /**
     * Send a message about a value fetch operation.
     * @param node
     * @param variableName
     * @param value
     */
    protected void sendFetchMessage(ICodeNode node, String variableName, Object value) {
        Object lineNumber = getLineNumber(node);

        if (lineNumber != null) {
            sendMessage(new Message(FETCH, new Object[]{lineNumber, variableName, value}));
        }
    }

    protected void sendCallMessage(ICodeNode node, String routineName) {
        Object lineNumber = getLineNumber(node);

        if (lineNumber != null) {
            sendMessage(new Message(CALL, new Object[]{lineNumber, routineName}));
        }
    }

    protected void sendReturnMessage(ICodeNode node, String routineName) {
        Object lineNumber = getLineNumber(node);

        if (lineNumber != null) {
            sendMessage(new Message(RETURN, new Object[] {lineNumber, routineName}));
        }
    }

    private Object getLineNumber(ICodeNode node) {
        Object lineNumber = null;

        while ((node != null) && ((lineNumber = node.getAttribute(LINE)) == null)) {
            node = node.getParent();
        }
        return lineNumber;
    }
}