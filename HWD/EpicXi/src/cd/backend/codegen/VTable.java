package cd.backend.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.VariableSymbol;

public class VTable {
	
	public int classNumber = 0;
	
	//Fields
	Map<String, VariableSymbol> fields = new LinkedHashMap<String, VariableSymbol>();
	
	//Methods
	Map<String, MethodSymbol> methodSymbols = new LinkedHashMap<String, MethodSymbol>();
}
