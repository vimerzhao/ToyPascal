package wci.frontend.pascal.parsers;

import wci.frontend.Token;
import wci.frontend.pascal.PascalParserTD;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.Predefined;
import wci.intermediate.symtabimpl.RoutineCodeImpl;

import static java.lang.Enum.valueOf;
import static wci.frontend.pascal.PascalErrorCode.INVALID_TYPE;
import static wci.frontend.pascal.PascalErrorCode.WRONG_NUMBER_OF_PARMS;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.ID;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.CALL;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_CODE;
import static wci.intermediate.typeimpl.TypeFormImpl.ENUMERATION;

public class CallStandardParser extends CallParser {
    /**
     * Constructor.
     *
     * @param parent the parent parser.
     */
    public CallStandardParser(PascalParserTD parent) {
        super(parent);
    }

    /**
     * Parse a call to a declared procedure or function.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    @Override
    public ICodeNode parse(Token token) throws Exception {
        ICodeNode callNode = ICodeFactory.createICodeNode(CALL);
        SymTabEntry pfId = symTabStack.lookup(token.getText().toLowerCase());
        RoutineCode routineCode = (RoutineCode) pfId.getAttribute(ROUTINE_CODE);
        callNode.setAttribute(ID, pfId);

        token = nextToken();
        switch ((RoutineCodeImpl) routineCode) {
            case READ:
            case READLN:  return parseReadReadln(token, callNode, pfId);

            case WRITE:
            case WRITELN: return parseWriteWriteln(token, callNode, pfId);

            case EOF:
            case EOLN:    return parseEofEoln(token, callNode, pfId);

            case ABS:
            case SQR:     return parseAbsSqr(token, callNode, pfId);

            case ARCTAN:
            case COS:
            case EXP:
            case LN:
            case SIN:
            case SQRT:    return parseArctanCosExpLnSinSqrt(token, callNode,
                    pfId);

            case PRED:
            case SUCC:    return parsePredSucc(token, callNode, pfId);

            case CHR:     return parseChr(token, callNode, pfId);
            case ODD:     return parseOdd(token, callNode, pfId);
            case ORD:     return parseOrd(token, callNode, pfId);

            case ROUND:
            case TRUNC:   return parseRoundTrunc(token, callNode, pfId);

            default:      return null;  // should never get here
        }
    }

    /**
     *
     * @param token
     * @param callNode
     * @param pfId
     * @return
     * @throws Exception
     */
    private ICodeNode parseReadReadln(Token token, ICodeNode callNode, SymTabEntry pfId) throws Exception {
        ICodeNode parmsNode = parseActualParameters(token, pfId, false, true, false);
        callNode.addChild(parmsNode);

        if ((pfId == Predefined.readId) && (callNode.getChildren().size() == 0)) {
            errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
        }
        return callNode;
    }

    private ICodeNode parseWriteWriteln(Token token, ICodeNode callNode, SymTabEntry pfId) throws Exception {
        ICodeNode parmsNode = parseActualParameters(token, pfId, false, false, true);
        callNode.addChild(parmsNode);

        if ((pfId == Predefined.writeId) && (callNode.getChildren().size() == 0)) {
            errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
        }
        return callNode;
    }

    private ICodeNode parseEofEoln(Token token, ICodeNode callNode, SymTabEntry pfId) throws Exception {
        ICodeNode parmsNode = parseActualParameters(token, pfId, false, false, false);
        callNode.addChild(parmsNode);
        if (checkParmCount(token, parmsNode, 0)) {
            callNode.setTypeSpec(Predefined.booleanType);
        }
        return callNode;
    }

    private ICodeNode parseAbsSqr(Token token, ICodeNode callNode, SymTabEntry pfId) throws Exception {
        ICodeNode parmsNode = parseActualParameters(token, pfId, false, false, false);
        callNode.addChild(parmsNode);
        if (checkParmCount(token, parmsNode, 1)) {
            TypeSpec argType = parmsNode.getChildren().get(0).getTypeSpec().baseType();
            if ((argType == Predefined.integerType) || (argType == Predefined.realType)) {
                callNode.setTypeSpec(argType);
            } else {
                errorHandler.flag(token, INVALID_TYPE, this);
            }
        }
        return callNode;
    }

    private ICodeNode parseArctanCosExpLnSinSqrt(Token token, ICodeNode callNode, SymTabEntry pfId) throws Exception {
        ICodeNode parmsNode = parseActualParameters(token, pfId, false, false, false);
        callNode.addChild(parmsNode);
        if (checkParmCount(token, parmsNode, 1)) {
            TypeSpec argType = parmsNode.getChildren().get(0).getTypeSpec().baseType();
            if ((argType == Predefined.integerType) || (argType == Predefined.realType)) {
                callNode.setTypeSpec(Predefined.realType);
            } else {
                errorHandler.flag(token, INVALID_TYPE, this);
            }
        }
        return callNode;
    }

    private ICodeNode parsePredSucc(Token token, ICodeNode callNode, SymTabEntry pfId) throws Exception {
        ICodeNode parmsNode = parseActualParameters(token, pfId, false, false, false);
        callNode.addChild(parmsNode);
        if (checkParmCount(token, parmsNode, 1)) {
            TypeSpec argType = parmsNode.getChildren().get(0).getTypeSpec().baseType();
            if ((argType == Predefined.integerType) || (argType.getForm() == ENUMERATION)) {
                callNode.setTypeSpec(argType);
            } else {
                errorHandler.flag(token, INVALID_TYPE, this);
            }
        }
        return callNode;
    }

    private ICodeNode parseChr(Token token, ICodeNode callNode, SymTabEntry pfId) throws Exception {
        ICodeNode parmsNode = parseActualParameters(token, pfId, false, false, false);
        callNode.addChild(parmsNode);
        if (checkParmCount(token, parmsNode, 1)) {
            TypeSpec argType = parmsNode.getChildren().get(0).getTypeSpec().baseType();
            if (argType == Predefined.integerType) {
                callNode.setTypeSpec(Predefined.charType);
            } else {
                errorHandler.flag(token, INVALID_TYPE, this);
            }
        }
        return callNode;
    }

    /**
     * Parse a cal to odd.
     * @param token
     * @param callNode
     * @param pfId
     * @return
     * @throws Exception
     */
    private ICodeNode parseOdd(Token token, ICodeNode callNode, SymTabEntry pfId) throws Exception {
        ICodeNode parmsNode = parseActualParameters(token, pfId, false, false, false);
        callNode.addChild(parmsNode);
        if (checkParmCount(token, parmsNode, 1)) {
            TypeSpec argType = parmsNode.getChildren().get(0).getTypeSpec().baseType();
            if (argType == Predefined.integerType) {
                callNode.setTypeSpec(Predefined.booleanType);
            } else {
                errorHandler.flag(token, INVALID_TYPE, this);
            }
        }
        return callNode;
    }

    /**
     * Parse a call to ord
     * @param token
     * @param callNode the CALL node.
     * @param pfId the symbol table entry of the standard routine name.
     * @return ICodeNode the CALL node.
     * @throws Exception
     */
    private ICodeNode parseOrd(Token token, ICodeNode callNode, SymTabEntry pfId) throws Exception {
        ICodeNode parmsNode = parseActualParameters(token, pfId, false, false, false);
        callNode.addChild(parmsNode);

        if (checkParmCount(token, parmsNode, 1)) {
            TypeSpec argType = parmsNode.getChildren().get(0).getTypeSpec().baseType();
            if ((argType == Predefined.charType) || (argType.getForm() == ENUMERATION)) {
                callNode.setTypeSpec(Predefined.integerType);
            } else {
                errorHandler.flag(token, INVALID_TYPE, this);
            }
        }
        return callNode;
    }

    /**
     * Parse a call to round or trunc.
     * @param token
     * @param callNode the CALL node
     * @param pfId the symbol table entry of the standard routine name.
     * @return ICodeNode the CALL node.
     * @throws Exception
     */
    private ICodeNode parseRoundTrunc(Token token, ICodeNode callNode, SymTabEntry pfId) throws Exception {
        ICodeNode parmsNode = parseActualParameters(token, pfId, false, false, false);

        callNode.addChild(parmsNode);

        // There should be one real parameter.
        if (checkParmCount(token, parmsNode, 1)) {
            TypeSpec argType = parmsNode.getChildren().get(0).getTypeSpec().baseType();
            if (argType == Predefined.realType) {
                callNode.setTypeSpec(Predefined.integerType);
            } else {
                errorHandler.flag(token, INVALID_TYPE, this);
            }
        }
        return callNode;
    }

    /**
     * Check the number of actual parameters
     * @param token the current token.
     * @param parmsNode the PARAMETERS node.
     * @param count the correct number of parameters.
     * @return true if the count if correct.
     */
    private boolean checkParmCount(Token token, ICodeNode parmsNode, int count) {
        if (((parmsNode == null) && count == 0) || (parmsNode.getChildren().size() == count)) {
            return true;
        } else {
            errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
            return false;
        }
    }
}
