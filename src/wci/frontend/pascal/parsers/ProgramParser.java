package wci.frontend.pascal.parsers;

import wci.frontend.Token;
import wci.frontend.pascal.PascalParserTD;
import wci.frontend.pascal.PascalTokenType;
import wci.intermediate.SymTabEntry;

import java.util.EnumSet;

import static wci.frontend.pascal.PascalErrorCode.MISSING_PERIOD;
import static wci.frontend.pascal.PascalTokenType.*;

public class ProgramParser extends DeclarationsParser {
    // Synchronization set to start a program.
    static final EnumSet<PascalTokenType> PROGRAM_START_SET =
            EnumSet.of(PROGRAM, SEMICOLON);

    static {
        PROGRAM_START_SET.addAll(DeclarationsParser.DECLARATION_START_SET);
    }

    public ProgramParser(PascalParserTD parent) {
        super(parent);
    }

    /**
     * Parse a program.
     *
     * @param token    the initial token.
     * @param parentId the symbol table entry of the parent routine's name
     * @return null
     * @throws Exception if an error occurred.
     */
    @Override
    public SymTabEntry parse(Token token, SymTabEntry parentId) throws Exception {
        token = synchronize(PROGRAM_START_SET);

        // Parse the program
        DeclaredRoutineParser routineParser = new DeclaredRoutineParser(this);
        routineParser.parse(token, parentId);

        // Look for the final period
        token = currentToken();
        if (token.getType() != DOT) {
            errorHandler.flag(token, MISSING_PERIOD, this);
        }
        return null;
    }
}
