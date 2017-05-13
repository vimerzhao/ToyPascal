package wci.backend.interpreter.debuggerimpl;

import wci.backend.Backend;
import wci.backend.interpreter.Debugger;
import wci.backend.interpreter.RuntimeStack;
import wci.intermediate.Definition;
import wci.intermediate.SymTabEntry;
import wci.message.Message;

import java.util.ArrayList;

/**
 * CommandLineDebugger
 * <p>
 * Command line version of the interactive source-level debugger.
 */
public class CommandLineDebugger extends Debugger {
    private CommandProcessor commandProcessor;

    /**
     * Constructor.
     *
     * @param backend      the back end.
     * @param runtimeStack the runtime stack.
     */
    public CommandLineDebugger(Backend backend, RuntimeStack runtimeStack) {
        super(backend, runtimeStack);
        commandProcessor = new CommandProcessor(this);
    }

    @Override
    public void processMessage(Message message) {
        commandProcessor.processMessage(message);
    }

    @Override
    public void promptForCommand() {
        System.out.print("(pdb) ");
    }

    @Override
    public boolean parseCommand() {
        return commandProcessor.parseCommand();
    }

    @Override
    public void atStatement(Integer lineNumber) {
        System.out.println("\nAt line" + lineNumber);
    }

    @Override
    public void atBreakpoint(Integer lineNumber) {
        System.out.println("\nBreakpoint at line " + lineNumber);
    }

    @Override
    public void atWatchpointValue(Integer lineNumber, String name, Object value) {
        System.out.println("\nAt line " + lineNumber + ":" + name + ": " + value.toString());
    }

    @Override
    public void atWatchpointAssignment(Integer lineNumber, String name, Object value) {
        System.out.println("\nAt line " + lineNumber + ": " + name + ":= " + value.toString());
    }

    @Override
    public void callRoutine(Integer lineNumber, String routineName) {
    }

    @Override
    public void returnRoutine(Integer lineNumber, String routineName) {
    }

    @Override
    public void displayValue(String valueString) {
        System.out.println(valueString);
    }

    @Override
    public void displayCallStack(ArrayList stack) {
        for (Object item : stack) {
            // Name of a procedure or function.
            if (item instanceof SymTabEntry) {
                SymTabEntry routineId = (SymTabEntry) item;
                String routineName = routineId.getName();
                int level = routineId.getSymTab().getNestingLevel();
                Definition definition = routineId.getDefinition();

                System.out.println(level + ": " + definition.getText().toUpperCase() + " " + routineName);
            } else if (item instanceof NameValuePair) { // Variable name-value pair.
                NameValuePair pair = (NameValuePair) item;
                System.out.print(" " + pair.getVariableName() + ": ");
                displayValue(pair.getValueString());
            }
        }
    }

    @Override
    public void quit() {
        System.out.println("Program terminated.");
        System.exit(-1);
    }

    @Override
    public void commandError(String errorMessage) {
        System.out.println("!!! ERROR: " + errorMessage);
    }

    @Override
    public void runtimeError(String errorMessage, Integer lineNumber) {
        System.out.print("!!! RUNTIME ERROR");
        if (lineNumber != null) {
            System.out.print(" at line " + String.format("%03d", lineNumber));
        }
        System.out.println(": " + errorMessage);
    }
}
