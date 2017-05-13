package wci.backend.interpreter;

import java.util.ArrayList;

/**
 * Interface for the interpreter's runtime memory map.
 */
public interface MemoryMap {
    /**
     * Return the memory cell with the given name.
     *
     * @param name
     * @return
     */
    Cell getCell(String name);

    /**
     * @return the list of all the names.
     */
    ArrayList<String> getAllNames();
}
