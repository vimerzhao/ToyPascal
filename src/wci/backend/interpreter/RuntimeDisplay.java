package wci.backend.interpreter;

/**
 * Interface for the interpreter's runtime display.
 */
public interface RuntimeDisplay {
    /**
     * Get the activation record at a given nesting level.
     *
     * @param nestingLevel the nesting level.
     * @return the activation record.
     */
    ActivationRecord getActivationRecord(int nestingLevel);

    /**
     * Update the display for a call to a routine at a given nesting level.
     *
     * @param nestingLevel the nesting level.
     * @param ar           the activation record for the routine.
     */
    void callUpdate(int nestingLevel, ActivationRecord ar);

    /**
     * Update the display for a return from a routine at a given nesting level.
     *
     * @param nestingLevel
     */
    void returnUpdate(int nestingLevel);
}
