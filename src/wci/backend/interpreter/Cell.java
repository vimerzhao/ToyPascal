package wci.backend.interpreter;

public interface Cell {
    /**
     * Set a new value into the cell.
     * @param newValue
     */
    void setValue(Object newValue);

    Object getValue();
}
