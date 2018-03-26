// Generated from /home/frankie/Dropbox/ETH/INFK/2018 FS/Compiler Design/compiler-design/HW2/src/cd/frontend/parser/Javali.g4 by ANTLR 4.7
package cd.frontend.parser;

	// Java header
	// package cd.frontend.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JavaliParser}.
 */
public interface JavaliListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JavaliParser#unit}.
	 * @param ctx the parse tree
	 */
	void enterUnit(JavaliParser.UnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#unit}.
	 * @param ctx the parse tree
	 */
	void exitUnit(JavaliParser.UnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void enterClassDecl(JavaliParser.ClassDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void exitClassDecl(JavaliParser.ClassDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#memberList}.
	 * @param ctx the parse tree
	 */
	void enterMemberList(JavaliParser.MemberListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#memberList}.
	 * @param ctx the parse tree
	 */
	void exitMemberList(JavaliParser.MemberListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(JavaliParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(JavaliParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#methodDecl}.
	 * @param ctx the parse tree
	 */
	void enterMethodDecl(JavaliParser.MethodDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#methodDecl}.
	 * @param ctx the parse tree
	 */
	void exitMethodDecl(JavaliParser.MethodDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#formalParamList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParamList(JavaliParser.FormalParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#formalParamList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParamList(JavaliParser.FormalParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(JavaliParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(JavaliParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#stmtBlock}.
	 * @param ctx the parse tree
	 */
	void enterStmtBlock(JavaliParser.StmtBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#stmtBlock}.
	 * @param ctx the parse tree
	 */
	void exitStmtBlock(JavaliParser.StmtBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#methodCallStmt}.
	 * @param ctx the parse tree
	 */
	void enterMethodCallStmt(JavaliParser.MethodCallStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#methodCallStmt}.
	 * @param ctx the parse tree
	 */
	void exitMethodCallStmt(JavaliParser.MethodCallStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#assignmentStmt}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentStmt(JavaliParser.AssignmentStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#assignmentStmt}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentStmt(JavaliParser.AssignmentStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#writeStmt}.
	 * @param ctx the parse tree
	 */
	void enterWriteStmt(JavaliParser.WriteStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#writeStmt}.
	 * @param ctx the parse tree
	 */
	void exitWriteStmt(JavaliParser.WriteStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(JavaliParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(JavaliParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(JavaliParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(JavaliParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#returnStmt}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(JavaliParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#returnStmt}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(JavaliParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#newExpr}.
	 * @param ctx the parse tree
	 */
	void enterNewExpr(JavaliParser.NewExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#newExpr}.
	 * @param ctx the parse tree
	 */
	void exitNewExpr(JavaliParser.NewExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#readExpr}.
	 * @param ctx the parse tree
	 */
	void enterReadExpr(JavaliParser.ReadExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#readExpr}.
	 * @param ctx the parse tree
	 */
	void exitReadExpr(JavaliParser.ReadExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#methodCallExpr}.
	 * @param ctx the parse tree
	 */
	void enterMethodCallExpr(JavaliParser.MethodCallExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#methodCallExpr}.
	 * @param ctx the parse tree
	 */
	void exitMethodCallExpr(JavaliParser.MethodCallExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#actualParamList}.
	 * @param ctx the parse tree
	 */
	void enterActualParamList(JavaliParser.ActualParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#actualParamList}.
	 * @param ctx the parse tree
	 */
	void exitActualParamList(JavaliParser.ActualParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierAccess(JavaliParser.IdentifierAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierAccess(JavaliParser.IdentifierAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BRACKETS}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBRACKETS(JavaliParser.BRACKETSContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BRACKETS}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBRACKETS(JavaliParser.BRACKETSContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LITERAL}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLITERAL(JavaliParser.LITERALContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LITERAL}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLITERAL(JavaliParser.LITERALContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IDENTIFIERACCESS}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIDENTIFIERACCESS(JavaliParser.IDENTIFIERACCESSContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IDENTIFIERACCESS}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIDENTIFIERACCESS(JavaliParser.IDENTIFIERACCESSContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UNARYOP}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterUNARYOP(JavaliParser.UNARYOPContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UNARYOP}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitUNARYOP(JavaliParser.UNARYOPContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BINARYOP1}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBINARYOP1(JavaliParser.BINARYOP1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code BINARYOP1}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBINARYOP1(JavaliParser.BINARYOP1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code BINARYOP2}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBINARYOP2(JavaliParser.BINARYOP2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code BINARYOP2}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBINARYOP2(JavaliParser.BINARYOP2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code BINARYOP3}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBINARYOP3(JavaliParser.BINARYOP3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code BINARYOP3}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBINARYOP3(JavaliParser.BINARYOP3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code BINARYOP4}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBINARYOP4(JavaliParser.BINARYOP4Context ctx);
	/**
	 * Exit a parse tree produced by the {@code BINARYOP4}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBINARYOP4(JavaliParser.BINARYOP4Context ctx);
	/**
	 * Enter a parse tree produced by the {@code BINARYOP5}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBINARYOP5(JavaliParser.BINARYOP5Context ctx);
	/**
	 * Exit a parse tree produced by the {@code BINARYOP5}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBINARYOP5(JavaliParser.BINARYOP5Context ctx);
	/**
	 * Enter a parse tree produced by the {@code REFERENCETYPE}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterREFERENCETYPE(JavaliParser.REFERENCETYPEContext ctx);
	/**
	 * Exit a parse tree produced by the {@code REFERENCETYPE}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitREFERENCETYPE(JavaliParser.REFERENCETYPEContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BINARYOP6}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBINARYOP6(JavaliParser.BINARYOP6Context ctx);
	/**
	 * Exit a parse tree produced by the {@code BINARYOP6}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBINARYOP6(JavaliParser.BINARYOP6Context ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(JavaliParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(JavaliParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#referenceType}.
	 * @param ctx the parse tree
	 */
	void enterReferenceType(JavaliParser.ReferenceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#referenceType}.
	 * @param ctx the parse tree
	 */
	void exitReferenceType(JavaliParser.ReferenceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(JavaliParser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#arrayType}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(JavaliParser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaliParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(JavaliParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaliParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(JavaliParser.LiteralContext ctx);
}