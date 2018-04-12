package cd.frontend.semantic;

import cd.ir.Symbol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TypeManager {

    private Map<String, Symbol.ClassSymbol> classes = new HashMap<>();

    /**
     * Adds a type to the known types. Arrays don't count as a new type.
     * @param type The type that should be added.
     * @throws SemanticFailure with cause DOUBLE_DECLARATION if the type already exists in the lists of types
     * @throws SemanticFailure with cause OBJECT_CLASS_DEFINED if a type tries to declare Object
     */
    public void addType(Symbol.ClassSymbol type)  throws SemanticFailure {
        if (classes.containsKey(type.name)) {
            throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
        }
        if (type.name.equals("Object")) {
            throw new SemanticFailure(SemanticFailure.Cause.OBJECT_CLASS_DEFINED);
        }
        classes.put(type.name, type);
    }

    /**
     * Returns the list of all defined types.
     * @return
     */
    public Collection<Symbol.ClassSymbol> getTypes() {
        return classes.values();
    }

    /**
     * Checks whether a type is assignable to another.
     * @param variable
     * @param expr
     * @return True if ...
     * @throws SemanticFailure with cause TYPE_ERROR
     */
    public boolean isAssignable(Symbol.TypeSymbol variable, Symbol.TypeSymbol expr) throws SemanticFailure {
        if (variable instanceof Symbol.PrimitiveTypeSymbol){
            if(expr instanceof Symbol.PrimitiveTypeSymbol){
                return true;
            }else {
                throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
            }
        }
        if(expr instanceof Symbol.PrimitiveTypeSymbol){
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }
        Symbol.ClassSymbol assignedTo = (Symbol.ClassSymbol) variable;
        Symbol.ClassSymbol assignedType = (Symbol.ClassSymbol) expr;
        while (assignedType != Symbol.ClassSymbol.objectType){
            if(assignedType == assignedTo){
                return true;
            }
            assignedType = assignedType.superClass;
        }
        throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
    }

    /**
     * Get a method of a class
     * @param name The name of the method
     * @param receiver The receiver object symbol on which the method should be executed.
     * @return The found method symbol
     * @throws SemanticFailure with cause TYPE_ERROR if a method is called on a primitive type or if the method was not
     * found
     */
    public Symbol.MethodSymbol getMethod(String name, Symbol.TypeSymbol receiver) throws SemanticFailure {
        if (receiver instanceof Symbol.PrimitiveTypeSymbol) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        } else {
            Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) receiver;
            if (!classes.containsValue(receiver)) {
                throw new SemanticFailure(SemanticFailure.Cause.NO_SUCH_TYPE);
            }
            while (classSymbol != Symbol.ClassSymbol.objectType) {
                if (classSymbol.methods.containsKey(name)) {
                    return classSymbol.methods.get(name);
                }
                classSymbol = classSymbol.superClass;

            }
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }
    }

    /**
     * Converts a string to a type symbol.
     * @param typeName The name of the type.
     * @return The corresponding type
     * @throws SemanticFailure if the requested type does not exist.
     */
    public Symbol.TypeSymbol stringToTypeSymbol(String typeName) throws SemanticFailure {
        Symbol.TypeSymbol type;

        boolean isArray = false;

        if (typeName.endsWith("[]")) {
            isArray = true;
            typeName = typeName.substring(0, typeName.length() - 2);
        }

        switch (typeName) {
            case "int":
                type = Symbol.PrimitiveTypeSymbol.intType;
                break;
            case "void":
                type = Symbol.PrimitiveTypeSymbol.voidType;
                break;
            case "boolean":
                type = Symbol.PrimitiveTypeSymbol.booleanType;
                break;
            case "Object":
                type = Symbol.ClassSymbol.objectType;
                break;
            default:
                if (!classes.containsKey(typeName)) {
                    throw new SemanticFailure(SemanticFailure.Cause.NO_SUCH_TYPE);
                }
                type = classes.get(typeName);
                break;
        }

        if (isArray) {
            type = new Symbol.ArrayTypeSymbol(type);
        }

        return type;
    }
}
