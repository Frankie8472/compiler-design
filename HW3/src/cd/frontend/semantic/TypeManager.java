package cd.frontend.semantic;

import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.TypeSymbol;
import cd.ir.Symbol.PrimitiveTypeSymbol;
import cd.ir.Symbol.ArrayTypeSymbol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TypeManager {

    private Map<String, ClassSymbol> classes = new HashMap<>();

    /**
     * Adds a type to the known types. Arrays don't count as a new type.
     *
     * @param type The type that should be added.
     * @throws SemanticFailure with cause DOUBLE_DECLARATION if the type already exists in the lists of types
     * @throws SemanticFailure with cause OBJECT_CLASS_DEFINED if a type tries to declare Object
     */
    public void addType(ClassSymbol type) throws SemanticFailure {
        if (classes.containsKey(type.name)) {
            throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
        }
        if (type.name.equals("Object")) {
            throw new SemanticFailure(SemanticFailure.Cause.OBJECT_CLASS_DEFINED);
        }
        classes.put(type.name, type);
    }

    /**
     * @return The list of all defined types
     */
    public Collection<ClassSymbol> getTypes() {
        return classes.values();
    }

    /**
     * Checks whether a type is assignable to another.
     *
     * @param variable Type it should be
     * @param expr     Type it actually is
     * @return True if it is assignable, else False
     * @throws SemanticFailure with cause TYPE_ERROR
     */
    public boolean isAssignable(TypeSymbol variable, TypeSymbol expr) throws SemanticFailure {
        if (variable instanceof PrimitiveTypeSymbol) {
            if (expr instanceof PrimitiveTypeSymbol) {
                return true; //todo: chönnd all primitive types vonenand erbe?
            } else {
                throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
            }
        }

        if (expr instanceof PrimitiveTypeSymbol) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }

        ClassSymbol assignedTo = (ClassSymbol) variable;
        ClassSymbol assignedType = (ClassSymbol) expr;

        while (assignedType != ClassSymbol.objectType) {
            if (assignedType == assignedTo) {
                return true;
            }
            assignedType = assignedType.superClass;
        }

        throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
    }

    /**
     * Get a method of a class
     *
     * @param name     The name of the method
     * @param receiver The receiver object symbol on which the method should be executed.
     * @return The found method symbol
     * @throws SemanticFailure with cause TYPE_ERROR if a method is called on a primitive type or if the method was not
     *                         found.
     */
    public MethodSymbol getMethod(String name, TypeSymbol receiver) throws SemanticFailure {
        if (receiver instanceof PrimitiveTypeSymbol) {
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
        }

        ClassSymbol classSymbol = (ClassSymbol) receiver;

        if (!classes.containsValue(receiver)) {
            throw new SemanticFailure(SemanticFailure.Cause.NO_SUCH_TYPE);
        }

        while (classSymbol != ClassSymbol.objectType) {
            if (classSymbol.methods.containsKey(name)) {
                return classSymbol.methods.get(name);
            }
            classSymbol = classSymbol.superClass;

        }

        throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);

    }

    /**
     * Converts a string to a TypeSymbol.
     * Gets existing class from class-table.
     *
     * @param typeName The name of the type.
     * @return The corresponding type as of class TypeSymbol
     * @throws SemanticFailure if the requested type does not exist.
     */
    public TypeSymbol stringToTypeSymbol(String typeName) throws SemanticFailure {
        TypeSymbol type;

        boolean isArray = false;

        if (typeName.endsWith("[]")) {
            isArray = true;
            typeName = typeName.substring(0, typeName.length() - 2);
        }

        switch (typeName) {
            case "int":
                type = PrimitiveTypeSymbol.intType;
                break;
            case "void":
                type = PrimitiveTypeSymbol.voidType;
                break;
            case "boolean":
                type = PrimitiveTypeSymbol.booleanType;
                break;
            case "Object":
                type = ClassSymbol.objectType;
                break;
            case "<null>":
                type = ClassSymbol.nullType;
                break;
            default:
                if (!classes.containsKey(typeName)) {
                    throw new SemanticFailure(SemanticFailure.Cause.NO_SUCH_TYPE);
                }
                type = classes.get(typeName);
                break;
        }

        if (isArray) {
            type = new ArrayTypeSymbol(type);
        }

        return type;
    }
}
