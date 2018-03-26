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
	 * Enter a parse tree produced by the {@code remoteMethodCall}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void enterRemoteMethodCall(JavaliParser.RemoteMethodCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code remoteMethodCall}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void exitRemoteMethodCall(JavaliParser.RemoteMethodCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void enterFieldAccess(JavaliParser.FieldAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void exitFieldAccess(JavaliParser.FieldAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void enterVarAccess(JavaliParser.VarAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void exitVarAccess(JavaliParser.VarAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code thisAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void enterThisAccess(JavaliParser.ThisAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code thisAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void exitThisAccess(JavaliParser.ThisAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void enterArrayAccess(JavaliParser.ArrayAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void exitArrayAccess(JavaliParser.ArrayAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code localMethodCall}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void enterLocalMethodCall(JavaliParser.LocalMethodCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code localMethodCall}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 */
	void exitLocalMethodCall(JavaliParser.LocalMethodCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprUnaryOp}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprUnaryOp(JavaliParser.ExprUnaryOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprUnaryOp}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprUnaryOp(JavaliParser.ExprUnaryOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprBOpComp}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprBOpComp(JavaliParser.ExprBOpCompContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprBOpComp}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprBOpComp(JavaliParser.ExprBOpCompContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprBOpEq}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprBOpEq(JavaliParser.ExprBOpEqContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprBOpEq}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprBOpEq(JavaliParser.ExprBOpEqContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprBOpAdd}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprBOpAdd(JavaliParser.ExprBOpAddContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprBOpAdd}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprBOpAdd(JavaliParser.ExprBOpAddContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprCast}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprCast(JavaliParser.ExprCastContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprCast}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprCast(JavaliParser.ExprCastContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprIdentifierAccess}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprIdentifierAccess(JavaliParser.ExprIdentifierAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprIdentifierAccess}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprIdentifierAccess(JavaliParser.ExprIdentifierAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprBOpOr}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprBOpOr(JavaliParser.ExprBOpOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprBOpOr}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprBOpOr(JavaliParser.ExprBOpOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprLiteral}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprLiteral(JavaliParser.ExprLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprLiteral}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprLiteral(JavaliParser.ExprLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprBOpMult}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprBOpMult(JavaliParser.ExprBOpMultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprBOpMult}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprBOpMult(JavaliParser.ExprBOpMultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprInBrackets}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprInBrackets(JavaliParser.ExprInBracketsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprInBrackets}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprInBrackets(JavaliParser.ExprInBracketsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprBOpAnd}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExprBOpAnd(JavaliParser.ExprBOpAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprBOpAnd}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExprBOpAnd(JavaliParser.ExprBOpAndContext ctx);
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