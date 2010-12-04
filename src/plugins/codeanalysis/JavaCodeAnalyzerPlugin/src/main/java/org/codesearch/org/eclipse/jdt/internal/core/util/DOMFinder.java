/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.codesearch.org.eclipse.jdt.internal.core.util;

import org.codesearch.org.eclipse.jdt.core.IInitializer;
import org.codesearch.org.eclipse.jdt.core.IMember;
import org.codesearch.org.eclipse.jdt.core.ISourceRange;
import org.codesearch.org.eclipse.jdt.core.JavaModelException;
import org.codesearch.org.eclipse.jdt.core.dom.ASTNode;
import org.codesearch.org.eclipse.jdt.core.dom.ASTVisitor;
import org.codesearch.org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.codesearch.org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.codesearch.org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.codesearch.org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.codesearch.org.eclipse.jdt.core.dom.CompilationUnit;
import org.codesearch.org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.codesearch.org.eclipse.jdt.core.dom.EnumDeclaration;
import org.codesearch.org.eclipse.jdt.core.dom.IBinding;
import org.codesearch.org.eclipse.jdt.core.dom.ImportDeclaration;
import org.codesearch.org.eclipse.jdt.core.dom.Initializer;
import org.codesearch.org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.codesearch.org.eclipse.jdt.core.dom.MethodDeclaration;
import org.codesearch.org.eclipse.jdt.core.dom.NormalAnnotation;
import org.codesearch.org.eclipse.jdt.core.dom.PackageDeclaration;
import org.codesearch.org.eclipse.jdt.core.dom.ParameterizedType;
import org.codesearch.org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.codesearch.org.eclipse.jdt.core.dom.TypeDeclaration;
import org.codesearch.org.eclipse.jdt.core.dom.TypeParameter;
import org.codesearch.org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.codesearch.org.eclipse.jdt.internal.core.SourceRefElement;

public class DOMFinder extends ASTVisitor {

	public ASTNode foundNode = null;
	public IBinding foundBinding = null;

	private CompilationUnit ast;
	private SourceRefElement element;
	private boolean resolveBinding;
	private int rangeStart = -1, rangeLength = 0;

	public DOMFinder(CompilationUnit ast, SourceRefElement element, boolean resolveBinding) {
		this.ast = ast;
		this.element = element;
		this.resolveBinding = resolveBinding;
	}

	protected boolean found(ASTNode node, ASTNode name) {
		if (name.getStartPosition() == this.rangeStart && name.getLength() == this.rangeLength) {
			this.foundNode = node;
			return true;
		}
		return false;
	}

	public ASTNode search() throws JavaModelException {
		ISourceRange range = null;
		if (this.element instanceof IMember && !(this.element instanceof IInitializer))
			range = ((IMember) this.element).getNameRange();
		else
			range = this.element.getSourceRange();
		this.rangeStart = range.getOffset();
		this.rangeLength = range.getLength();
		this.ast.accept(this);
		return this.foundNode;
	}

	public boolean visit(AnnotationTypeDeclaration node) {
		if (found(node, node.getName()) && this.resolveBinding)
			this.foundBinding = node.resolveBinding();
		return true;
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		if (found(node, node.getName()) && this.resolveBinding)
			this.foundBinding = node.resolveBinding();
		return true;
	}

	public boolean visit(AnonymousClassDeclaration node) {
		ASTNode name;
		ASTNode parent = node.getParent();
		switch (parent.getNodeType()) {
			case ASTNode.CLASS_INSTANCE_CREATION:
				name = ((ClassInstanceCreation) parent).getType();
				if (name.getNodeType() == ASTNode.PARAMETERIZED_TYPE) {
					name = ((ParameterizedType) name).getType();
				}
				break;
			case ASTNode.ENUM_CONSTANT_DECLARATION:
				name = ((EnumConstantDeclaration) parent).getName();
				break;
			default:
				return true;
		}
		if (found(node, name) && this.resolveBinding)
			this.foundBinding = node.resolveBinding();
		return true;
	}

	public boolean visit(EnumConstantDeclaration node) {
		if (found(node, node.getName()) && this.resolveBinding)
			this.foundBinding = node.resolveVariable();
		return true;
	}

	public boolean visit(EnumDeclaration node) {
		if (found(node, node.getName()) && this.resolveBinding)
			this.foundBinding = node.resolveBinding();
		return true;
	}

	public boolean visit(ImportDeclaration node) {
		if (found(node, node) && this.resolveBinding)
			this.foundBinding = node.resolveBinding();
		return true;
	}

	public boolean visit(Initializer node) {
		// note that no binding exists for an Initializer
		found(node, node);
		return true;
	}

	public boolean visit(MarkerAnnotation node) {
		if (found(node, node) && this.resolveBinding)
			this.foundBinding = node.resolveAnnotationBinding();
		return true;
	}

	public boolean visit(MethodDeclaration node) {
		if (found(node, node.getName()) && this.resolveBinding)
			this.foundBinding = node.resolveBinding();
		return true;
	}

	public boolean visit(NormalAnnotation node) {
		if (found(node, node) && this.resolveBinding)
			this.foundBinding = node.resolveAnnotationBinding();
		return true;
	}

	public boolean visit(PackageDeclaration node) {
		if (found(node, node) && this.resolveBinding)
			this.foundBinding = node.resolveBinding();
		return true;
	}

	public boolean visit(SingleMemberAnnotation node) {
		if (found(node, node) && this.resolveBinding)
			this.foundBinding = node.resolveAnnotationBinding();
		return true;
	}

	public boolean visit(TypeDeclaration node) {
		if (found(node, node.getName()) && this.resolveBinding)
			this.foundBinding = node.resolveBinding();
		return true;
	}

	public boolean visit(TypeParameter node) {
		if (found(node, node.getName()) && this.resolveBinding)
			this.foundBinding = node.resolveBinding();
		return true;
	}

	public boolean visit(VariableDeclarationFragment node) {
		if (found(node, node.getName()) && this.resolveBinding)
			this.foundBinding = node.resolveBinding();
		return true;
	}
}
