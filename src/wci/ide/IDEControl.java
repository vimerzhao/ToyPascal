package wci.ide;

import java.awt.Color;

/**
 * <h1>IDEControl</h1>
 *
 * <p>The master interface of the Pascal IDE.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public interface IDEControl
{
    // Debugger output line tags.
    String LISTING_TAG = "!LISTING:";
    String PARSER_TAG = "!PARSER:";
    String SYNTAX_TAG = "!SYNTAX:";
    String INTERPRETER_TAG = "!INTERPRETER:";

    String DEBUGGER_AT_TAG = "!DEBUGGER.AT:";
    String DEBUGGER_BREAK_TAG = "!DEBUGGER.BREAK:";
    String DEBUGGER_ROUTINE_TAG = "!DEBUGGER.ROUTINE:";
    String DEBUGGER_VARIABLE_TAG = "!DEBUGGER.VARIABLE:";


}
