package wci.backend.interpreter.memoryimpl;

import wci.backend.interpreter.ActivationRecord;
import wci.backend.interpreter.RuntimeDisplay;

import java.util.ArrayList;

public class RuntimeDisplayImpl extends ArrayList<ActivationRecord> implements RuntimeDisplay {

    public RuntimeDisplayImpl() {
        add(null);  // dummy element 0(never used)
    }

    @Override
    public ActivationRecord getActivationRecord(int nestingLevel) {
        return get(nestingLevel);
    }

    /**
     * Update the display for a call to a routine at a given nesting level.
     * @param nestingLevel the nesting level.
     * @param ar the activation record for the routine.
     */
    @Override
    public void callUpdata(int nestingLevel, ActivationRecord ar) {
        if (nestingLevel >= size()) {
            add(ar);
        } else {
            ActivationRecord prevAr = get(nestingLevel);
            set(nestingLevel, ar.makeLinkTo(prevAr));
        }
    }

    /**
     * Update the display for a return from a routine at a given nesting level.
     * @param nestingLevel
     */
    @Override
    public void returnUpdate(int nestingLevel) {
        int topIndex = size() - 1;
        ActivationRecord ar = get(nestingLevel);
        ActivationRecord prevAr = ar.linkedTo();

        if (prevAr != null) {
            set(nestingLevel, prevAr);
        } else if (nestingLevel == topIndex) {
            remove(topIndex);
        }
    }
}
