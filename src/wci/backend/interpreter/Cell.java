package wci.backend.interpreter;

public interface Cell {
    Object getValue();

    /**
     * Set a new value into the cell.
     *
     * @param newValue
     */
    void setValue(Object newValue);
}
