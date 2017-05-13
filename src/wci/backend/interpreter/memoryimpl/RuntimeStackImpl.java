package wci.backend.interpreter.memoryimpl;

import wci.backend.interpreter.ActivationRecord;
import wci.backend.interpreter.MemoryFactory;
import wci.backend.interpreter.RuntimeDisplay;
import wci.backend.interpreter.RuntimeStack;

import java.util.ArrayList;

public class RuntimeStackImpl extends ArrayList<ActivationRecord> implements RuntimeStack {
    private RuntimeDisplay display;

    public RuntimeStackImpl() {
        display = MemoryFactory.createRuntimeDisplay();
    }

    @Override
    public ArrayList<ActivationRecord> records() {
        return this;
    }

    @Override
    public ActivationRecord getTopmost(int nestingLevel) {
        return display.getActivationRecord(nestingLevel);
    }

    @Override
    public int currentNestingLevel() {
        int topIndex = size() - 1;
        return topIndex >= 0 ? get(topIndex).getNestingLevel() : -1;
    }

    @Override
    public void pop() {
        display.returnUpdate(currentNestingLevel());
        remove(size() - 1);
    }

    @Override
    public void push(ActivationRecord ar) {
        int nestingLevel = ar.getNestingLevel();

        add(ar);
        display.callUpdate(nestingLevel, ar);
    }
}
