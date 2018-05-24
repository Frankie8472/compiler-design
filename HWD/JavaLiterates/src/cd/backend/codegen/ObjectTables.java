package cd.backend.codegen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import cd.ir.Symbol.ArrayTypeSymbol;
import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.PrimitiveTypeSymbol;
import cd.ir.Symbol.TypeSymbol;
import cd.ir.Symbol.VariableSymbol;

public class ObjectTables {
	AstCodeGenerator cg;
	HashMap<String, ObjectTable> ot = new HashMap<String, ObjectTable>();

	public ObjectTables(AstCodeGenerator acg) {
		this.cg = acg;
		generateObjectTables();
	}

	public void generateObjectTables() {
		for (TypeSymbol type : cg.typeSymbols.map.values()) {
			if (type instanceof ClassSymbol && !type.name.equals("Object")) {
				ot.put(type.name, new ObjectTable((ClassSymbol) type, false));
			} else if (type instanceof ArrayTypeSymbol && !type.name.equals("Object")
					&& !(((ArrayTypeSymbol) type).elementType instanceof PrimitiveTypeSymbol)) {
				ot.put(type.name, new ObjectTable((ClassSymbol) ((ArrayTypeSymbol) type).elementType, true));
			}
		}

		// add boolean and int
		ot.put("int", new ObjectTable("int", false));
		ot.put("boolean", new ObjectTable("boolean", false));
		ot.put("int[]", new ObjectTable("int[]", true));
		ot.put("boolean[]", new ObjectTable("boolean[]", true));
	}

	// Emits vtables for each class
	public void constructVtables() {
		// Emit vtable for object
		cg.emit.labelDI(AstCodeGenerator.VTABLE_PREFIX + "Object");
		cg.emit.rawDI(".int " + "0");
		
		//Emit primitive vtables
		cg.emit.labelDI(AstCodeGenerator.VTABLE_PREFIX + "int");
		cg.emit.rawDI(".int " + "0");
		
		cg.emit.labelDI(AstCodeGenerator.VTABLE_PREFIX + "boolean");
		cg.emit.rawDI(".int " + "0");

		for (ObjectTable oTable : ot.values()) {
			if (oTable.primitive || oTable.array)
				continue;
			// Emitting vtable for this class according to hw4-slides.pdf page 16:
			cg.emit.labelDI(AstCodeGenerator.VTABLE_PREFIX + oTable.name);
			// Emitt super class vtable link
			if (!oTable.classSym.superClass.name.equals("Object"))
				cg.emit.rawDI(".int " + AstCodeGenerator.VTABLE_PREFIX + oTable.classSym.superClass.name);
			else
				cg.emit.rawDI(".int " + AstCodeGenerator.VTABLE_PREFIX + "Object");

			for (MethodSymbol methodS : oTable.methodL) {
				cg.emit.comment("The Offset is: " + oTable.methodOffset.get(methodS.name));
				cg.emit.rawDI(".int " + methodS.context.name + "_" + methodS.name);
			}
		}
	}

	public String getMethodLabel(ClassSymbol classSym, String str) {
		return classSym.name + "_" + str;

	}

}

class ObjectTable {
	public String name;
	public ArrayList<VariableSymbol> fieldL = new ArrayList<VariableSymbol>();
	public ArrayList<MethodSymbol> methodL = new ArrayList<MethodSymbol>();
	public HashMap<String, Integer> methodOffset;
	public HashMap<String, Integer> fieldOffsets;
	public ClassSymbol classSym;
	public boolean primitive;
	public boolean array;

	public ObjectTable(ClassSymbol c, boolean array) {
		primitive = false;
		this.array = array;
		if (array)
			this.name = c.name + "[]";
		else
			this.name = c.name;

		this.classSym = c;

		methodOffset = new HashMap<String, Integer>();
		fieldOffsets = new HashMap<String, Integer>();

		generateFieldOffsets();
		generateMethodOffsets();

		generateLocalAndParaOffsets();
	}

	public ObjectTable(String name, boolean array) {
		primitive = true;
		this.array = array;
	}

	public int objSize() {
		if (primitive)
			return 4;

		// Size of object table; 1 for pointer to vtable
		int n = 1;
		ClassSymbol symb = classSym;

		// Increase object table size by 1 per field directly in the class
		n = n + symb.fields.keySet().size();

		/* Increase object table size for each field inherited */
		ClassSymbol currentSym = symb.superClass;

		// Iterate until at Object
		while (currentSym != null && !currentSym.name.equals("Object")) {
			n = n + currentSym.fields.keySet().size();
			currentSym = currentSym.superClass;
		}

		return n * 4;
	}

	private void generateLocalAndParaOffsets() {
		int localOff;
		int paraOff;

		for (MethodSymbol methSym : methodL) {
			localOff = -4;
			paraOff = 12;

			// Handle locals
			for (VariableSymbol varSym : methSym.locals.values()) {
				methSym.locAndParaOffsets.put(varSym.name, localOff);

				localOff = localOff - 4;
			}

			// Handle parameters
			for (VariableSymbol varSym : methSym.parameters) {
				methSym.locAndParaOffsets.put(varSym.name, paraOff);

				paraOff = paraOff + 4;
			}
		}

	}

	void generateFieldOffsets() {
		// The first 4 bytes are for the vtable pointer
		int offset = 4;

		ArrayList<VariableSymbol> fieldList = new ArrayList<VariableSymbol>();
		// We want more "general" fields (fields inherited from higher up the
		// inheritence tree) to be placed as high up as possible
		// For this we collect the fields in the normal order first
		ClassSymbol theElder = classSym;
		while (theElder != null && !theElder.name.equals("Object")) {

			ArrayList<VariableSymbol> varList = new ArrayList<VariableSymbol>(theElder.fields.values());
			Collections.sort(varList, new Comparator<VariableSymbol>() {
				@Override
				public int compare(VariableSymbol o1, VariableSymbol o2) {
					return o1.name.compareTo(o2.name);
				}
			});

			for (VariableSymbol varSym : varList) {
				fieldList.add(varSym);
			}

			theElder = theElder.superClass;
		}

		// We reverse the list so that the fields from the oldest ancestor appear first
		Collections.reverse(fieldList);
		fieldL = fieldList;

		// Now we put the offsets into the hash map
		for (VariableSymbol varSym : fieldL) {
			fieldOffsets.put(varSym.name, offset);
			offset = offset + 4;
		}
	}

	void generateMethodOffsets() {
		// The first 4 bytes are for the pointer to the parent vtable
		int offset = 4;

		ClassSymbol currentSym = classSym;

		// Keep track of all methods written to avoid including an overwritten method
		// from a parent
		ArrayList<String> writtenMethods = new ArrayList<String>();
		ArrayList<MethodSymbol> methodList = new ArrayList<MethodSymbol>();

		while (currentSym != null && !currentSym.name.equals("Object")) {
			ArrayList<MethodSymbol> methList = new ArrayList<MethodSymbol>(currentSym.methods.values());
			Collections.sort(methList, new Comparator<MethodSymbol>() {
				@Override
				public int compare(MethodSymbol o1, MethodSymbol o2) {
					return o1.name.compareTo(o2.name);
				}
			});

			for (MethodSymbol mSym : methList) {
				String methodName = mSym.name;
				// check if this method was overwritten
				if (!writtenMethods.contains(methodName)) {
					// Add its context to the methodSymbol
					currentSym.methods.get(methodName).context = currentSym;

					writtenMethods.add(methodName);
					methodList.add(currentSym.methods.get(methodName));
				}

			}

			currentSym = currentSym.superClass;
		}

		// We reverse the list so that the methods from the oldest ancestor appear first
		Collections.reverse(methodList);
		methodL = methodList;

		// Now we put the offsets into the hash map
		for (MethodSymbol methodSym : methodL) {
			methodOffset.put(methodSym.name, offset);
			offset = offset + 4;
		}
	}

}