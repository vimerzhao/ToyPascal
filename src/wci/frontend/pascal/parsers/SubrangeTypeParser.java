package wci.frontend.pascal.parsers;

import wci.frontend.Token;
import wci.frontend.TokenType;
import wci.frontend.pascal.PascalParserTD;
import wci.intermediate.TypeFactory;
import wci.intermediate.TypeSpec;
import wci.intermediate.symtabimpl.Predefined;

import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.frontend.pascal.PascalTokenType.DOT_DOT;
import static wci.frontend.pascal.PascalTokenType.IDENTIFIER;
import static wci.intermediate.typeimpl.TypeFormImpl.ENUMERATION;
import static wci.intermediate.typeimpl.TypeFormImpl.SUBRANGE;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;

public class SubrangeTypeParser extends TypeSpecificationParser {
    /**
     * Constructor.
     *
     * @param parent the parent parser.
     */
    public SubrangeTypeParser(PascalParserTD parent) {
        super(parent);
    }

    /**
     * Parse a Pascal subrange type specification.
     *
     * @param token the current token.
     * @return the subrange type specification.
     * @throws Exception if an error occurred.
     */
    public TypeSpec parse(Token token) throws Exception {
        TypeSpec subrangeType = TypeFactory.createType(SUBRANGE);
        Object maxValue = null;
        Object minValue = null;
        // Parse the minimum constant.
        Token constantToken = token;
        ConstantDefinitionsParser constantParser = new ConstantDefinitionsParser(this);
        minValue = constantParser.parseConstant(token);

        // Set the minimum constant's type.
        TypeSpec minType = constantToken.getType() == IDENTIFIER
                ? constantParser.getConstantType(constantToken)
                : constantParser.getConstantType(minValue);
        minValue = checkValueType(constantToken, minValue, minType);

        token = currentToken();
        Boolean sawDotDot = false;

        // Look for the .. token.
        if (token.getType() == DOT_DOT) {
            token = nextToken();
            sawDotDot = true;
        }

        TokenType tokenType = token.getType();

        // At the start of the maximum constant?
        if (ConstantDefinitionsParser.CONSTANT_START_SET.contains(tokenType)) {
            if (!sawDotDot) {
                errorHandler.flag(token, MISSING_DOT_DOT, this);
            }

            // Parse the maximum constant.
            token = synchronize(ConstantDefinitionsParser.CONSTANT_START_SET);
            constantToken = token;
            maxValue = constantParser.parseConstant(token);

            // Set the maximum constant's type.
            TypeSpec maxType = constantToken.getType() == IDENTIFIER
                    ? constantParser.getConstantType(constantToken)
                    : constantParser.getConstantType(maxValue);
            maxValue = checkValueType(constantToken, maxValue, maxType);

            // Are the min and max value types valid?
            if ((minType == null) || (maxType == null)) {
                errorHandler.flag(constantToken, INCOMPATIBLE_TYPES, this);
            } else if (minType != maxType) {
                errorHandler.flag(constantToken, INVALID_SUBRANGE_TYPE, this);
            } else if ((minType != null) && (maxType != null) &&
                    ((Integer) minValue >= (Integer) maxValue)) {
                errorHandler.flag(constantToken, MIN_GT_MAX, this);
            }
        } else {
            errorHandler.flag(constantToken, INVALID_SUBRANGE_TYPE, this);
        }
        subrangeType.setAttribute(SUBRANGE_BASE_TYPE, minType);
        subrangeType.setAttribute(SUBRANGE_MIN_VALUE, minValue);
        subrangeType.setAttribute(SUBRANGE_MAX_VALUE, maxValue);

        return subrangeType;
    }

    /**
     * Check a value of a type specification.
     *
     * @param token the current token.
     * @param value the value.
     * @param type  the specification.
     * @return the value.
     */
    private Object checkValueType(Token token, Object value, TypeSpec type) {
        if (type == null) {
            return value;
        }
        if (type == Predefined.integerType) {
            return value;
        } else if (type == Predefined.charType) {
            char ch = ((String) value).charAt(0);
            return Character.getNumericValue(ch);
        } else if (type.getForm() == ENUMERATION) {
            return value;
        } else {
            errorHandler.flag(token, INVALID_SUBRANGE_TYPE, this);
            return value;
        }
    }
}
