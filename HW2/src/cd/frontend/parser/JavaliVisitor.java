// Generated from /home/frankie/Dropbox/ETH/INFK/2018 FS/Compiler Design/compiler-design/HW2/src/cd/frontend/parser/Javali.g4 by ANTLR 4.7
package cd.frontend.parser;

	// Java header
	// package cd.frontend.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JavaliParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface JavaliVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link JavaliParser#unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnit(JavaliParser.UnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#classDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDecl(JavaliParser.ClassDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#memberList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberList(JavaliParser.MemberListContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(JavaliParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#methodDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDecl(JavaliParser.MethodDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#formalParamList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParamList(JavaliParser.FormalParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(JavaliParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#stmtBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtBlock(JavaliParser.StmtBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#methodCallStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCallStmt(JavaliParser.MethodCallStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#assignmentStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStmt(JavaliParser.AssignmentStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#writeStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWriteStmt(JavaliParser.WriteStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#ifStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(JavaliParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#whileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(JavaliParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#returnStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(JavaliParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#newExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewExpr(JavaliParser.NewExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#readExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReadExpr(JavaliParser.ReadExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#methodCallExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCallExpr(JavaliParser.MethodCallExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#actualParamList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActualParamList(JavaliParser.ActualParamListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code remoteMethodCall}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRemoteMethodCall(JavaliParser.RemoteMethodCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code fieldAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldAccess(JavaliParser.FieldAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarAccess(JavaliParser.VarAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code thisAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThisAccess(JavaliParser.ThisAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayAccess}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayAccess(JavaliParser.ArrayAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code localMethodCall}
	 * labeled alternative in {@link JavaliParser#identifierAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalMethodCall(JavaliParser.LocalMethodCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprUnaryOp}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprUnaryOp(JavaliParser.ExprUnaryOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprBOpComp}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBOpComp(JavaliParser.ExprBOpCompContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprBOpEq}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBOpEq(JavaliParser.ExprBOpEqContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprBOpAdd}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBOpAdd(JavaliParser.ExprBOpAddContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprCast}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprCast(JavaliParser.ExprCastContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprIdentifierAccess}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprIdentifierAccess(JavaliParser.ExprIdentifierAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprBOpOr}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBOpOr(JavaliParser.ExprBOpOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprLiteral}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprLiteral(JavaliParser.ExprLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprBOpMult}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBOpMult(JavaliParser.ExprBOpMultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprInBrackets}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprInBrackets(JavaliParser.ExprInBracketsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprBOpAnd}
	 * labeled alternative in {@link JavaliParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBOpAnd(JavaliParser.ExprBOpAndContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(JavaliParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#referenceType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReferenceType(JavaliParser.ReferenceTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#arrayType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayType(JavaliParser.ArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaliParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(JavaliParser.LiteralContext ctx);
}