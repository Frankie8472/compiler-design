package cd.frontend.semantic;

import cd.ir.Symbol;

import java.util.HashMap;
import java.util.Map;

public class TypeManager {

    private Map<String, Symbol.ClassSymbol> classes = new HashMap<>();

    public void addType(Symbol.ClassSymbol type){
        if (classes.containsKey(type.name)) {
            throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
        }
        if (type.name.equals("Object")){
            throw new SemanticFailure(SemanticFailure.Cause.OBJECT_CLASS_DEFINED);
        }
        classes.put(type.name, type);
    }

    public void isAssignable(Symbol.TypeSymbol variable, Symbol.TypeSymbol expr){

    }

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
                if(!classes.containsKey(typeName)){
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
