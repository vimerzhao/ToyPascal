package wci.backend.interpreter.memoryimpl;

import wci.backend.interpreter.Cell;

public class CellImpl implements Cell {
    private Object value = null;    // contained in the memory cell.

    public CellImpl(Object value) {
        this.value = value;
    }

    @Override
    public void setValue(Object newValue) {
        this.value = newValue;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
