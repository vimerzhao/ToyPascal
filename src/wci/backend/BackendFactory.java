package wci.backend;

import wci.backend.compiler.CodeGenerator;
import wci.backend.interpreter.Debugger;
import wci.backend.interpreter.DebuggerType;
import wci.backend.interpreter.Executor;
import wci.backend.interpreter.RuntimeStack;
import wci.backend.interpreter.debuggerimpl.CommandLineDebugger;
import wci.backend.interpreter.debuggerimpl.GUIDebugger;
import wci.intermediate.TypeSpec;
import wci.intermediate.symtabimpl.Predefined;

/**
 * BackendFactory
 *
 * A factory class that creates compiler and interpreter components.
 */
public class BackendFactory
{
    /**
     * Create a compiler or an interpreter back end component.
     * @param operation either "compile" or "execute"
     * @param inputPath the input file path.
     * @return a compiler or an interpreter back end component.
     * @throws Exception if an error occurred.
     */
    public static Backend createBackend(String operation,DebuggerType type, String inputPath) throws Exception {
        if (operation.equalsIgnoreCase("compile")) {
            return new CodeGenerator();
        }
        else if (operation.equalsIgnoreCase("execute")) {
            return new Executor(type, inputPath);
        }
        else {
            throw new Exception("Backend factory: Invalid operation '" +
                                operation + "'");
        }
    }

    /**
     * Return the default value for a data type.
     * @param type
     * @return
     */
    public static Object defaultValue(TypeSpec type) {
        type = type.baseType();

        if (type == Predefined.integerType) {
            return new Integer((0));
        } else if (type == Predefined.realType) {
            return new Float(0.0f);
        } else if (type == Predefined.booleanType) {
            return new Boolean(false);
        } else if (type == Predefined.charType) {
            return new Character('#');
        } else {
            return new String("#");
        }
    }

    /**
     * Create a debugger.
     * @param type the type of debugger(COMMAND_LINE or GUI).
     * @param backend the back end.
     * @param runtimeStack the runtime stack.
     * @return the debugger.
     */
    public static Debugger createDebugger(DebuggerType type, Backend backend, RuntimeStack runtimeStack) {
        switch (type) {
            case COMMAND_LINE: {
                return new CommandLineDebugger(backend, runtimeStack);
            }
            case GUI: {
                return new GUIDebugger(backend, runtimeStack);
            }
            default: {
                return null;
            }
        }
    }
}
