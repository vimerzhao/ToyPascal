package wci.frontend.pascal.parsers;

import wci.frontend.Token;
import wci.frontend.TokenType;
import wci.frontend.pascal.PascalParserTD;
import wci.frontend.pascal.PascalTokenType;
import wci.intermediate.Definition;
import wci.intermediate.SymTabEntry;
import wci.intermediate.TypeSpec;

import java.util.ArrayList;
import java.util.EnumSet;

import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.frontend.pascal.PascalTokenType.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.FIELD;
import static wci.intermediate.symtabimpl.DefinitionImpl.PROGRAM_PARM;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;

public class VariableDeclarationsParser extends DeclarationsParser {
    private Definition definition;  // how to define the identifier.

    public VariableDeclarationsParser(PascalParserTD parent) {
        super(parent);
    }

    /**
     * Setter.
     * @param definition the definition to set.
     */
    protected void setDefinition(Definition definition) {
        this.definition = definition;
    }


    // Synchronization set for a variable identifier.
    static final EnumSet<PascalTokenType> IDENTIFIER_SET =
            DeclarationsParser.VAR_START_SET.clone();
    static {
        IDENTIFIER_SET.add(IDENTIFIER);
        IDENTIFIER_SET.add(END);
        IDENTIFIER_SET.add(SEMICOLON);
    }

    // Synchronization set for the start of the next definition or declaration.
    static final EnumSet<PascalTokenType> NEXT_START_SET =
            DeclarationsParser.ROUTINE_START_SET.clone();
    static {
        NEXT_START_SET.add(IDENTIFIER);
        NEXT_START_SET.add(SEMICOLON);
    }

    // Synchronization set to start a sublist identifier.
    static final EnumSet<PascalTokenType> IDENTIFIER_START_SET =
            EnumSet.of(IDENTIFIER, COMMA);
    // Synchronization set to follow a sublist identifier.
    private static final EnumSet<PascalTokenType> IDENTIFIER_FOLLOW_SET =
            EnumSet.of(COLON, SEMICOLON);
    static {
        IDENTIFIER_FOLLOW_SET.addAll(DeclarationsParser.VAR_START_SET);
    }

    // Synchronization set the , token
    private static final EnumSet<PascalTokenType> COMMA_SET =
            EnumSet.of(COMMA, COLON, IDENTIFIER, SEMICOLON);

    /**
     * Parse variable declarations.
     * @param token the initial token.
     * @param parentId the symbol table of the parent's routine's name.
     * @return null
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry parentId) throws Exception {
        token = synchronize(IDENTIFIER_SET);

        // Loop to parse a sequence of variable declarations separated by semicolons.
        while (token.getType() == IDENTIFIER) {
            // Parse the identifier sublist and its type specification.
            parseIdentifierSublist(token, IDENTIFIER_FOLLOW_SET, COMMA_SET);
            token = currentToken();
            TokenType tokenType = token.getType();

            if (tokenType == SEMICOLON) {
                while (token.getType() == SEMICOLON) {
                    token = nextToken();
                }
            } else if (NEXT_START_SET.contains(tokenType)) {
                // If at the start of the next definition or declaration,
                // then missing a semicolon.
                errorHandler.flag(token, MISSING_SEMICOLON, this);
            }

            token = synchronize(IDENTIFIER_SET);
        }
        return null;
    }
    /**
     * Parse a sublist of identifier and their type specification.
     * @param token the current token.
     * @param followSet the synchronization set to follow an identifier.
     * @param commaSet
     * @return the sublist of identifiers in a declaration.
     * @throws Exception if an error occurred.
     */
    protected ArrayList<SymTabEntry> parseIdentifierSublist(Token token,
            EnumSet<PascalTokenType> followSet, EnumSet<PascalTokenType> commaSet) throws Exception {
        ArrayList<SymTabEntry> sublist = new ArrayList<>();
        do {
            token = synchronize(IDENTIFIER_START_SET);
            SymTabEntry id = parseIdentifier(token);

            if (id != null) {
                sublist.add(id);
            }

            token = synchronize(commaSet);
            TokenType tokenType = token.getType();

            if (tokenType == COMMA) {
                token = nextToken();
                if (followSet.contains(token.getType())) {
                    errorHandler.flag(token, MISSING_IDENTIFIER, this);
                }
            } else if (IDENTIFIER_START_SET.contains(tokenType)) {
                errorHandler.flag(token, MISSING_COMMA, this);
            }
        } while (!followSet.contains(token.getType()));

        if (definition != PROGRAM_PARM) {
            // Parse the type specification.
            TypeSpec type = parseTypeSpec(token);

            // Assign the type specification to each identifier in the list.
            for (SymTabEntry variableId : sublist) {
                variableId.setTypeSpec(type);
            }
        }

        return sublist;
    }

    /**
     * Parse an identifier.
     * @param token the current token.
     * @return the symbol table table entry of the identifier.
     * @throws Exception if an error occurred.
     */
    private SymTabEntry parseIdentifier(Token token) throws Exception {
        SymTabEntry id = null;
        if (token.getType() == IDENTIFIER) {
            String name = token.getText().toLowerCase();
            id = symTabStack.lookupLocal(name);

            if (id == null) {
                id = symTabStack.enterLocal(name);
                id.setDefinition(definition);
                id.appendLineNumber(token.getLineNumber());
            } else {
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
            }

            token = nextToken();
        } else {
            errorHandler.flag(token, MISSING_IDENTIFIER, this);
        }
        return id;
    }

    // Synchronization set for the : token.
    private static final EnumSet<PascalTokenType> COLON_SET =
            EnumSet.of(COLON, SEMICOLON);

    /**
     * Parse the type specification.
     * @param token the current token.
     * @return the type specification.
     * @throws Exception if an error occurred.
     */
    protected TypeSpec parseTypeSpec(Token token) throws Exception {
        token = synchronize(COLON_SET);
        if (token.getType() == COLON) {
            token = nextToken();
        } else {
            errorHandler.flag(token, MISSING_COLON, this);
        }

        // Parse the type specification.
        TypeSpecificationParser typeSpecificationParser = new TypeSpecificationParser(this);
        TypeSpec type = typeSpecificationParser.parse(token);

        if ((definition != VARIABLE) && (definition != FIELD)
                && (type != null) && (type.getIdentifier() == null)) {
             errorHandler.flag(token, INVALID_TYPE, this);
        }

        return type;
    }
}
