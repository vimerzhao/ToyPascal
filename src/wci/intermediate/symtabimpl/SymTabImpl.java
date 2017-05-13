package wci.intermediate.symtabimpl;

import wci.intermediate.SymTab;
import wci.intermediate.SymTabEntry;
import wci.intermediate.SymTabFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * SymTabImpl
 *
 * An implementation of the symbol table.
 */
public class SymTabImpl extends TreeMap<String, SymTabEntry> implements SymTab {
    private int nestingLevel;

    public SymTabImpl(int nestingLevel) {
        this.nestingLevel = nestingLevel;
    }

    /**
     * Getter.
     * @return the scope nesting level of this entry.
     */
    @Override
    public int getNestingLevel() {
        return nestingLevel;
    }

    /**
     * Create and enter a new entry into the symbol table.
     * @param name the name of the entry.
     * @return the new entry
     */
    @Override
    public SymTabEntry enter(String name) {
        SymTabEntry entry = SymTabFactory.createSymTabEntry(name, this);
        put(name, entry);

        return entry;
    }

    /**
     * Look up an existing symbol table entry.
     * @param name the name of the entry.
     * @return the entry,or null if it does not exist.
     */
    @Override
    public SymTabEntry lookup(String name) {
        return get(name);
    }

    /**
     * @return a list of symbol table entries sorted by name.
     */
    @Override
    public ArrayList<SymTabEntry> sortedEntries() {
        Collection<SymTabEntry> entries = values();
        Iterator<SymTabEntry> iterator = entries.iterator();
        ArrayList<SymTabEntry> list = new ArrayList<>(size());

        // Iterate over the sorted entries and append them to the list.
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

        return list;    // sorted list of entries
    }
}