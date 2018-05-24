package cd.backend.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cd.Config;
import cd.ir.Ast.ClassDecl;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.VarDecl;
import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.PrimitiveTypeSymbol;

public class VTableCreator {
	public List<ClassDecl> astRoots;
	
	public static List<VTable> noParent;
	
	
	public VTableCreator(List<ClassDecl> astRoots) {
		this.astRoots=(List<ClassDecl>) astRoots;
		noParent=new ArrayList<VTable>();
	}
	
	public Map<String,VTable> initVTables(){
		Map<String,VTable> result = new HashMap<String,VTable>();
		
		VTable.counter=0;
		
		VTable vtable;
		vtable= new VTable(ClassSymbol.objectType.name,false);
		result.put(vtable.typeSymbol, vtable);
		noParent.add(vtable);
		
		vtable= new VTable(ClassSymbol.objectType.name+"_array",true);
		result.put(vtable.typeSymbol, vtable);
		vtable.parent=result.get(ClassSymbol.objectType.name);
		
		vtable= new VTable(PrimitiveTypeSymbol.intType.name,false);
		result.put(vtable.typeSymbol, vtable);
		noParent.add(vtable);
		
		vtable= new VTable(PrimitiveTypeSymbol.intType.name+"_array",true);
		result.put(vtable.typeSymbol, vtable);
		vtable.parent=result.get(ClassSymbol.objectType.name);

		
		vtable= new VTable(PrimitiveTypeSymbol.booleanType.name,false);
		result.put(vtable.typeSymbol, vtable);
		noParent.add(vtable);
		
		vtable= new VTable(PrimitiveTypeSymbol.booleanType.name+"_array",true);
		result.put(vtable.typeSymbol, vtable);
		vtable.parent=result.get(ClassSymbol.objectType.name);

		
		vtable=new VTable("null",false);
		result.put(ClassSymbol.nullType.name, vtable);
		noParent.add(vtable);
		
		
		
		for(ClassDecl classDecl:astRoots) {
			vtable=new VTable(classDecl.sym.name,false);
			result.put(classDecl.name, vtable);
			
			vtable= new VTable(classDecl.sym.name+"_array",true);
			result.put(vtable.typeSymbol, vtable);
			vtable.parent=result.get(ClassSymbol.objectType.name);
			
			
		}
		
		for(ClassDecl classDecl:astRoots) {
			
			//add inheritance everywhere
			vtable=result.get(classDecl.name);
			vtable.parent=result.get(classDecl.superClass);
			
			//add in all fields from all parents
			addFieldsFromParents(classDecl,vtable);					
			
		}
		
		return result;
		
	}
	
	public void addFieldsFromParents(ClassDecl classDecl,VTable vtable) {
		
		if(classDecl.superClass.equals("Object")) {
			for(VarDecl varDecl:classDecl.fields()) {
				vtable.addVar(varDecl.sym);
			}
			
			
			for(MethodDecl methodDecl:classDecl.methods()) {
				
				vtable.addMethod(methodDecl.sym,"_"+classDecl.name+"_"+methodDecl.name);
				
				
			}
			
			return;
		}else {
			ClassDecl parent=classDecl.sym.superClass.ast;
			
			addFieldsFromParents(parent,vtable);
			
			for(VarDecl varDecl:classDecl.fields()) {
				vtable.addVar(varDecl.sym);
			}
			
			for(MethodDecl methodDecl:classDecl.methods()) {
				for(int i=0;i<vtable.methods.size();i++) {
					MethodSymbol sym=vtable.methods.get(i);
					if(overwrites(sym,methodDecl.sym)) {
						vtable.replaceMethod(sym, methodDecl.sym, "_"+classDecl.name+"_"+methodDecl.name);
					}else {
						
						vtable.addMethod(methodDecl.sym, "_"+classDecl.name+"_"+methodDecl.name);
						
					}
					
				}
			}
			return;

		}
	}
	
	public boolean overwrites(MethodSymbol m1,MethodSymbol m2) {
		boolean result = false;
		if(m1.name.equals(m2.name)) {
			if(m1.returnType==m2.returnType) {
				if(m1.parameters.size()==m2.parameters.size()) {
					result =true;
					for(int i=0;i<m1.parameters.size();i++) {
						if(m1.parameters.get(i)!=m2.parameters.get(i))
							result=false;
						
					}
				}
			}
		}
		
		return result;
	}
}
