package org.codesearch.javacodeanalyzerplugin.astanalyzer;

import java.beans.Visibility;
import java.util.LinkedList;
import java.util.List;
import org.codesearch.commons.plugins.codeanalyzing.ast.ASTElement;
import org.codesearch.commons.plugins.codeanalyzing.ast.FileNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.MethodNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Variable;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

public class JavaAstVisitor extends ASTVisitor {

    private FileNode fileNode;

    public JavaAstVisitor(FileNode fileNode) {
        this.fileNode = fileNode;
    }

    public void preVisit(ASTNode node) {
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(AnnotationTypeDeclaration node) {
        return true;
    }

    static private String getNodeAsString(ASTNode node) {

//		String className = node.getClass().getName();
//		int index = className.lastIndexOf(".");
//		if (index > 0)
//			className = className.substring(index+1);
//
//		if (node instanceof Comment)
//			return className;
//
//		String modifiers = "";
//		if (node instanceof BodyDeclaration) {
//			int mod = ((BodyDeclaration)node).getModifiers();
//			if (Modifier.isAbstract(mod))
//				modifiers += Modifier.ModifierKeyword.ABSTRACT_KEYWORD.toString() + ",";
//
//			if (Modifier.isFinal(mod))
//				modifiers += Modifier.ModifierKeyword.FINAL_KEYWORD.toString() + ",";
//
//			if (Modifier.isNative(mod))
//				modifiers += Modifier.ModifierKeyword.NATIVE_KEYWORD.toString() + ",";
//
//			if (Modifier.isPrivate(mod))
//				modifiers += Modifier.ModifierKeyword.PRIVATE_KEYWORD.toString() + ",";
//
//			if (Modifier.isProtected(mod))
//				modifiers += Modifier.ModifierKeyword.PROTECTED_KEYWORD.toString() + ",";
//
//			if (Modifier.isPublic(mod))
//				modifiers += Modifier.ModifierKeyword.PUBLIC_KEYWORD.toString() + ",";
//
//			if (Modifier.isStatic(mod))
//				modifiers += Modifier.ModifierKeyword.STATIC_KEYWORD.toString() + ",";
//
//			if (Modifier.isStrictfp(mod))
//				modifiers += Modifier.ModifierKeyword.STRICTFP_KEYWORD.toString() + ",";
//
//			if (Modifier.isSynchronized(mod))
//				modifiers += Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD.toString() + ",";
//
//			if (Modifier.isTransient(mod))
//				modifiers += Modifier.ModifierKeyword.TRANSIENT_KEYWORD.toString() + ",";
//
//			if (Modifier.isVolatile(mod))
//				modifiers += Modifier.ModifierKeyword.VOLATILE_KEYWORD.toString() + ",";
//		}
//
//		String toString = node.toString();
//		String value = "";
//		if (toString.startsWith(className)) {
//			if (modifiers.length() > 0) {
//				index = toString.indexOf("[");
//				if (index > 0)
//					value = toString.substring(0,index+1) + " " + modifiers + " " + toString.substring(index+1);
//				else
//					value = modifiers + " " + toString;
//			}
//			else
//				value = toString;
//		}
//		else {
//			value = className + "[";
//			if (modifiers.length() > 0)
//				value += " " + modifiers + " ";
//
//			value += toString + "]";
//		}

        String value = "";
        if (node instanceof TypeDeclaration) {
            return "Class defined at char: " + node.getStartPosition() + " " + (node.getStartPosition() + node.getLength());
        }
        return "";
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(AnonymousClassDeclaration node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ArrayAccess node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ArrayCreation node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ArrayInitializer node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ArrayType node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(AssertStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(Assignment node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(Block node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(BlockComment node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(BooleanLiteral node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(BreakStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(CastExpression node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(CatchClause node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(CharacterLiteral node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ClassInstanceCreation node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(CompilationUnit node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ConditionalExpression node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ConstructorInvocation node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ContinueStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(DoStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(EmptyStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(EnhancedForStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(EnumConstantDeclaration node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(EnumDeclaration node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ExpressionStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(FieldAccess node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(FieldDeclaration node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ForStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(IfStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ImportDeclaration node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(InfixExpression node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(InstanceofExpression node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(Initializer node) {
        return true;
    }

    /**
     * Visits the given AST node.
     * <p>
     * Unlike other node types, the boolean returned by the default
     * implementation is controlled by a constructor-supplied parameter
     * {@link #ASTVisitor(boolean) ASTVisitor(boolean)} which is
     * <code>false</code> by default. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @see #ASTVisitor()
     * @see #ASTVisitor(boolean)
     */
    public boolean visit(Javadoc node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(LabeledStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(LineComment node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(MarkerAnnotation node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(MemberRef node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(MemberValuePair node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(MethodRef node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(MethodRefParameter node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(MethodDeclaration node) {
        if (node.isConstructor()) {
            //TODO add handling for constructor invocation here
            return true;
        }
        MethodNode method = new MethodNode();
        method.setName(node.getName().getFullyQualifiedName());
        method.setDeclarationPosition(node.getStartPosition());
        List<Variable> parameters = new LinkedList<Variable>();
        List params = node.parameters();
        SingleVariableDeclaration currParameter = null;
        for (int i = 0; i < params.size(); i++) {
            currParameter = (SingleVariableDeclaration) params.get(i);
            Variable var = new Variable();
            var.setNodeLength(currParameter.getLength());
            var.setName(currParameter.getName().getIdentifier());
            var.setType(currParameter.getType().toString());
            var.setDeclarationPosition(currParameter.getStartPosition());
            parameters.add(var);
        }
        method.setDeclarationPosition(node.getStartPosition());
        method.setNodeLength(node.getLength());
        method.setParameters(parameters);
        method.setReturnType(node.getReturnType2().toString());
        Visibility visibility;
        //TODO figure out a way to retrieve visibility from method declaration
        fileNode.getNodes().add(method);
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(MethodInvocation node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(Modifier node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(NormalAnnotation node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(NullLiteral node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(NumberLiteral node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(PackageDeclaration node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(ParameterizedType node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ParenthesizedExpression node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(PostfixExpression node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(PrefixExpression node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(PrimitiveType node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(QualifiedName node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(QualifiedType node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ReturnStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(SimpleName node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(SimpleType node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(SingleMemberAnnotation node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(SingleVariableDeclaration node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(StringLiteral node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(SuperConstructorInvocation node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(SuperFieldAccess node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(SuperMethodInvocation node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(SwitchCase node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(SwitchStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(SynchronizedStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(TagElement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(TextElement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ThisExpression node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(ThrowStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(TryStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(TypeDeclaration node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(TypeDeclarationStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(TypeLiteral node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(TypeParameter node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(VariableDeclarationExpression node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(VariableDeclarationStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(VariableDeclarationFragment node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     */
    public boolean visit(WhileStatement node) {
        return true;
    }

    /**
     * Visits the given type-specific AST node.
     * <p>
     * The default implementation does nothing and return true. Subclasses may
     * reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @return <code>true</code> if the children of this node should be visited,
     *         and <code>false</code> if the children of this node should be
     *         skipped
     * @since 3.0
     */
    public boolean visit(WildcardType node) {
        return true;
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(AnnotationTypeDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(AnnotationTypeMemberDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(AnonymousClassDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ArrayAccess node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ArrayCreation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ArrayInitializer node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ArrayType node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(AssertStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(Assignment node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(Block node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(BlockComment node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(BooleanLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(BreakStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(CastExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(CatchClause node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(CharacterLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ClassInstanceCreation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(CompilationUnit node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ConditionalExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ConstructorInvocation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ContinueStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(DoStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(EmptyStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(EnhancedForStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(EnumConstantDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(EnumDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ExpressionStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(FieldAccess node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(FieldDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ForStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(IfStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ImportDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(InfixExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(InstanceofExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(Initializer node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(Javadoc node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(LabeledStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(LineComment node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(MarkerAnnotation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(MemberRef node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(MemberValuePair node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(MethodRef node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(MethodRefParameter node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(MethodDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(MethodInvocation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(Modifier node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(NormalAnnotation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(NullLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(NumberLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(PackageDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(ParameterizedType node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ParenthesizedExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(PostfixExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(PrefixExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(PrimitiveType node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(QualifiedName node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(QualifiedType node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ReturnStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(SimpleName node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(SimpleType node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(SingleMemberAnnotation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(SingleVariableDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(StringLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(SuperConstructorInvocation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(SuperFieldAccess node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(SuperMethodInvocation node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(SwitchCase node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(SwitchStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(SynchronizedStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(TagElement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(TextElement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ThisExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(ThrowStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(TryStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(TypeDeclaration node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(TypeDeclarationStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(TypeLiteral node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(TypeParameter node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(VariableDeclarationExpression node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(VariableDeclarationStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(VariableDeclarationFragment node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     */
    public void endVisit(WhileStatement node) {
        // default implementation: do nothing
    }

    /**
     * End of visit the given type-specific AST node.
     * <p>
     * The default implementation does nothing. Subclasses may reimplement.
     * </p>
     *
     * @param node
     *            the node to visit
     * @since 3.0
     */
    public void endVisit(WildcardType node) {
        // default implementation: do nothing
    }
}
