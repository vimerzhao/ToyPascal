package wci.intermediate.typeimpl;

import wci.intermediate.TypeForm;
import wci.intermediate.TypeSpec;
import wci.intermediate.symtabimpl.Predefined;

import static wci.intermediate.typeimpl.TypeFormImpl.ENUMERATION;
import static wci.intermediate.typeimpl.TypeFormImpl.SCALAR;

public class TypeChecker {
    /**
     * Check if a specification is integer.
     *
     * @param typeSpec the type specification.
     * @return true if integer, else false.
     */
    public static boolean isInteger(TypeSpec typeSpec) {
        return (typeSpec != null) && (typeSpec.baseType() == Predefined.integerType);
    }

    /**
     * Check if both type specification are integer.
     *
     * @param type1 the first type specification to check.
     * @param type2 the second type specification to check.
     * @return true if both are integer, else false.
     */
    public static boolean areBothInteger(TypeSpec type1, TypeSpec type2) {
        return isInteger(type1) && isInteger(type2);
    }

    /**
     * Check if a type specification is real.
     *
     * @param type the type specification to check.
     * @return true if real, else false.
     */
    public static boolean isReal(TypeSpec type) {
        return (type != null) && (type.baseType() == Predefined.realType);
    }

    /**
     * Check if a type specification is integer or real.
     *
     * @param type the type specification to check.
     * @return true if integer or real, else false.
     */
    public static boolean isIntegerOrReal(TypeSpec type) {
        return isInteger(type) || isReal(type);
    }

    /**
     * Check if at least one or two type specifications is real.
     *
     * @param type1 the first type specification to check.
     * @param type2 the second type specification to check.
     * @return true if at least one is real, else false.
     */
    public static boolean isAtLeastOneReal(TypeSpec type1, TypeSpec type2) {
        return (isReal(type1) && isReal(type2)) ||
                (isReal(type1) && isInteger(type2)) ||
                (isInteger(type1) && isReal(type2));
    }

    /**
     * Check if a type specification is boolean.
     *
     * @param typeSpec the type specification to check.
     * @return true if boolean, else false.
     */
    public static boolean isBoolean(TypeSpec typeSpec) {
        return (typeSpec != null) && (typeSpec.baseType() == Predefined.booleanType);
    }

    /**
     * Check if both type specification are boolean.
     *
     * @param type1 the first type specification to check.
     * @param type2 the second type specification to check.
     * @return true if both are boolean, else false.
     */
    public static boolean areBothBoolean(TypeSpec type1, TypeSpec type2) {
        return isBoolean(type1) && isBoolean(type2);
    }

    /**
     * Check if a type specification is Char.
     *
     * @param typeSpec the type specification to check.
     * @return true if char, else false.
     */
    public static boolean isChar(TypeSpec typeSpec) {
        return (typeSpec != null) && (typeSpec.baseType() == Predefined.charType);
    }

    /**
     * Check if two type specifications are assignment compatible.
     *
     * @param targetType the target type specification.
     * @param valueType  the value type specification.
     * @return true if the value can be assigned to the target, else false.
     */
    public static boolean areAssignmentCompatible(TypeSpec targetType, TypeSpec valueType) {
        if ((targetType == null) || (valueType == null)) {
            return false;
        }

        targetType = targetType.baseType();
        valueType = valueType.baseType();

        boolean compatible = false;

        if (targetType == valueType) {// Identical types.
            compatible = true;
        } else if (isReal(targetType) && isInteger(valueType)) {// real := integer
            compatible = true;
        } else {// string := string
            compatible = targetType.isPascalString() && valueType.isPascalString();
        }

        return compatible;
    }

    /**
     * Check if two type specifications are comparison compatible.
     *
     * @param type1 the first type specification to check.
     * @param type2 the second type specification to check.
     * @return true if the types can be compared to each other, else false.
     */
    public static boolean areComparisonCompatible(TypeSpec type1, TypeSpec type2) {
        if ((type1 == null) || (type2 == null)) {
            return false;
        }

        type1 = type1.baseType();
        type2 = type2.baseType();
        TypeForm form = type1.getForm();

        boolean compatible = false;

        if ((type1 == type2) && ((form == SCALAR) || (form == ENUMERATION))) {
            // Two identical scalar or enumeration types.
            compatible = true;
        } else if (isAtLeastOneReal(type1, type2)) {// One integer and one real.
            compatible = true;
        } else {// Two strings.
            compatible = type1.isPascalString() && type2.isPascalString();
        }

        return compatible;
    }
}
