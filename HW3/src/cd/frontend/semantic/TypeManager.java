package cd.frontend.semantic;

import cd.ir.Symbol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TypeManager {

    private Map<String, Symbol.ClassSymbol> classes = new HashMap<>();

    /**
     *
     * @param type
     */
    public void addType(Symbol.ClassSymbol type) {
        if (classes.containsKey(type.name)) {
            throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
        }
        if (type.name.equals("Object")) {
            throw new SemanticFailure(SemanticFailure.Cause.OBJECT_CLASS_DEFINED);
        }
        classes.put(type.name, type);
    }

    /**
     *
     * @return
     */
    public Collection<Symbol.ClassSymbol> getTypes() {
        return classes.values();
    }

    /**
     *
     * @param variable
     * @param expr
     * @return
     */
    public boolean isAssignable(Symbol.TypeSymbol variable, Symbol.TypeSymbol expr) {
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
     *
     * @param name
     * @param receiver
     * @return
     */
    public Symbol.MethodSymbol getMethod(String name, Symbol.TypeSymbol receiver) {
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
     *
     * @param typeName
     * @return
     */
    public Symbol.TypeSymbol stringToTypeSymbol(String typeName) {
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
