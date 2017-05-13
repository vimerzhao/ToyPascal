package wci.frontend.pascal.parsers;

import wci.frontend.Token;
import wci.frontend.TokenType;
import wci.frontend.pascal.PascalParserTD;
import wci.frontend.pascal.PascalTokenType;
import wci.intermediate.SymTabEntry;
import wci.intermediate.TypeFactory;
import wci.intermediate.TypeForm;
import wci.intermediate.TypeSpec;

import java.util.ArrayList;
import java.util.EnumSet;

import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.frontend.pascal.PascalTokenType.*;
import static wci.intermediate.typeimpl.TypeFormImpl.ARRAY;
import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;

public class ArrayTypeParser extends TypeSpecificationParser {
    // Synchronization set for the [ token.
    private static final EnumSet<PascalTokenType> LEFT_BRACKET_SET =
            SimpleTypeParser.SIMPLE_TYPE_START_SET.clone();
    // Synchronization set for the ] token.
    private static final EnumSet<PascalTokenType> RIGHT_BRACKET_SET =
            EnumSet.of(RIGHT_BRACKET, OF, SEMICOLON);
    // Synchronization set for OF.
    private static final EnumSet<PascalTokenType> OF_SET =
            TypeSpecificationParser.TYPE_START_SET.clone();
    // Synchronization set to start an index type.
    private static final EnumSet<PascalTokenType> INDEX_START_SET =
            SimpleTypeParser.SIMPLE_TYPE_START_SET.clone();
    // Synchronization set to end an index type.
    private static final EnumSet<PascalTokenType> INDEX_END_SET =
            EnumSet.of(RIGHT_BRACKET, OF, SEMICOLON);
    // Synchronization set to follow an index type.
    private static final EnumSet<PascalTokenType> INDEX_FOLLOW_SET =
            INDEX_START_SET.clone();

    static {
        LEFT_BRACKET_SET.add(LEFT_BRACKET);
        LEFT_BRACKET_SET.add(RIGHT_BRACKET);
    }

    static {
        OF_SET.add(OF);
        OF_SET.add(SEMICOLON);
    }

    static {
        INDEX_START_SET.add(COMMA);
    }

    static {
        INDEX_FOLLOW_SET.addAll(INDEX_END_SET);
    }

    public ArrayTypeParser(PascalParserTD parent) {
        super(parent);
    }

    /**
     * Parse a Pascal array type specification.
     *
     * @param token the current token.
     * @return the array type specification.
     * @throws Exception if an error occurred.
     */
    public TypeSpec parse(Token token) throws Exception {
        TypeSpec arrayType = TypeFactory.createType(ARRAY);
        token = nextToken();    // consume ARRAY

        // Synchronize at the [ token.
        token = synchronize(LEFT_BRACKET_SET);
        if (token.getType() != LEFT_BRACKET) {
            errorHandler.flag(token, MISSING_LEFT_BRACKET, this);
        }

        // Parse the list of index types.
        TypeSpec elementType = parseIndexTypeList(token, arrayType);

        // Synchronize at the ] token.
        token = synchronize(RIGHT_BRACKET_SET);
        if (token.getType() == RIGHT_BRACKET) {
            token = nextToken();
        } else {
            errorHandler.flag(token, MISSING_RIGHT_BRACKET, this);
        }

        // Synchronize at OF
        token = synchronize(OF_SET);
        if (token.getType() == OF) {
            token = nextToken();
        } else {
            errorHandler.flag(token, MISSING_OF, this);
        }

        // Parse the element type.
        elementType.setAttribute(ARRAY_ELEMENT_TYPE, parseElementType(token));

        return arrayType;
    }

    /**
     * Parse the list of index type specifications.
     *
     * @param token     the current token.
     * @param arrayType the current array type specification.
     * @return the element type specification.
     * @throws Exception if an error occurred.
     */
    private TypeSpec parseIndexTypeList(Token token, TypeSpec arrayType) throws Exception {
        TypeSpec elementType = arrayType;
        boolean anotherIndex = false;

        token = nextToken();    // consume [

        // Parse the list of index type specifications.
        do {
            anotherIndex = false;

            // Parse the index type.
            token = synchronize(INDEX_START_SET);
            parseIndexType(token, elementType);

            // Synchronize at the , token.
            token = synchronize(INDEX_FOLLOW_SET);
            TokenType tokenType = token.getType();
            if ((tokenType != COMMA) && (tokenType != RIGHT_BRACKET)) {
                if (INDEX_START_SET.contains(tokenType)) {
                    errorHandler.flag(token, MISSING_COMMA, this);
                    anotherIndex = true;
                }
            } else if (tokenType == COMMA) {
                // Create an ARRAY element type object
                // for each subsequent index type.
                TypeSpec newElementType = TypeFactory.createType(ARRAY);
                elementType.setAttribute(ARRAY_ELEMENT_TYPE, newElementType);
                ;
                elementType = newElementType;

                token = nextToken();    // consume ,
                anotherIndex = true;
            }
        } while (anotherIndex);

        return elementType;
    }

    /**
     * Parse an index type specification.
     *
     * @param token     the current token.
     * @param arrayType the current array type specification.
     * @throws Exception if an error occurred.
     */
    private void parseIndexType(Token token, TypeSpec arrayType) throws Exception {
        SimpleTypeParser simpleTypeParser = new SimpleTypeParser(this);
        TypeSpec indexType = simpleTypeParser.parse(token);
        arrayType.setAttribute(ARRAY_INDEX_TYPE, indexType);

        if (indexType == null) {
            return;
        }

        TypeForm form = indexType.getForm();
        int count = 0;

        // Check the index type and set the element count.
        if (form == SUBRANGE) {
            Integer minValue = (Integer) indexType.getAttribute(SUBRANGE_MIN_VALUE);
            Integer maxValue = (Integer) indexType.getAttribute(SUBRANGE_MAX_VALUE);

            if ((minValue != null) && (maxValue != null)) {
                count = maxValue - minValue + 1;
            }
        } else if (form == ENUMERATION) {
            ArrayList<SymTabEntry> constants = (ArrayList<SymTabEntry>) indexType.getAttribute(ENUMERATION_CONSTANTS);
            count = constants.size();
        } else {
            errorHandler.flag(token, INVALID_INDEX_TYPE, this);
        }
        arrayType.setAttribute(ARRAY_ELEMENT_COUNT, count);
    }

    /**
     * Parse the element type specification.
     *
     * @param token the current token
     * @return the element type specification.
     * @throws Exception if an error occurred.
     */
    private TypeSpec parseElementType(Token token) throws Exception {
        TypeSpecificationParser typeSpecificationParser = new TypeSpecificationParser(this);
        return typeSpecificationParser.parse(token);
    }
}
