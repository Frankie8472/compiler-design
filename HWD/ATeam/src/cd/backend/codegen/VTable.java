package cd.backend.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cd.ir.Symbol.TypeSymbol;
import cd.ir.Symbol.VariableSymbol;
import cd.util.Pair;
import cd.Config;
import cd.ir.Symbol.MethodSymbol;

public class VTable {
	static int counter;
	
	//TypeSymbol sym;
	
	String typeSymbol;
	
	int id;
	
	boolean isArray;
			
	VTable parent;
	
	public List<VariableSymbol> variables;
	
	public List<MethodSymbol> methods;
	
	public List<String> labels;
	
	
	public VTable(String symbol,boolean array) {
		this.typeSymbol=symbol;
		this.id=counter;
		this.isArray=array;
		counter++;
		variables=new ArrayList<VariableSymbol>();
		methods=new ArrayList<MethodSymbol>();
		labels=new ArrayList<String>();
	}
	
	public void addVar(VariableSymbol s) {
		variables.add(s);
	}
	
	public void addMethod(MethodSymbol s,String label) {
		methods.add(s);
		labels.add(label);
	}
	
	public void replaceMethod(MethodSymbol oldsym,MethodSymbol newsym,String newLabel) {
		int index=methods.indexOf(oldsym);
		
		methods.set(index, newsym);
		labels.set(index, newLabel);
	}
	
	public String getLabel(MethodSymbol s) {
		return labels.get(methods.indexOf(s));
	}
	
	public int instanceSize() {
		return 4+variables.size()*Config.SIZEOF_PTR;
	}
	
	public int getOffset(VariableSymbol s) {
		return Config.SIZEOF_PTR+variables.indexOf(s)*Config.SIZEOF_PTR;
	}
	
	public int getOffset(MethodSymbol s) {
		return 3*Config.SIZEOF_PTR+methods.indexOf(s)*Config.SIZEOF_PTR;
	}
	
	public int vtableSize() {
		return 3*Config.SIZEOF_PTR+methods.size()*Config.SIZEOF_PTR;
	}
}
