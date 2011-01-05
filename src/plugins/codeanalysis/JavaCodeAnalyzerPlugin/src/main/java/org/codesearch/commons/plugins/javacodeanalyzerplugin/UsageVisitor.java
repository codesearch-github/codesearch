/**
 * Copyright 2010 David Froehlich   <david.froehlich@businesssoftware.at>,
 *                Samuel Kogler     <samuel.kogler@gmail.com>,
 *                Stephan Stiboller <stistc06@htlkaindorf.at>
 *
 * This file is part of Codesearch.
 *
 * Codesearch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codesearch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codesearch.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.javacodeanalyzerplugin;
//TODO implement ArrayCreationExpr
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.LinkedList;
import java.util.List;
import org.codesearch.commons.plugins.codeanalyzing.ast.Visibility;

/**
 * a visitor used to visit the AST created by the JavaParser
 * extracts all important information and creates a simplified AST
 * @author David Froehlich
 */
public class UsageVisitor extends VoidVisitorAdapter {

    private String packageName;
    private AnalyzerUtil util;
    private List<String> typeDeclarations = new LinkedList<String>();
    private List<String> imports = new LinkedList<String>();

    public UsageVisitor(AnalyzerUtil util, String packageName) {
        this.util = util;
        this.packageName = packageName;
    }

    @Override
    public void visit(ImportDeclaration n, Object arg) {
        super.visit(n, arg);
        String importName = n.getName().toString();
        if (n.isAsterisk()) {
            importName += ".*";
        }
        imports.add(importName);
    }

    @Override
    public void visit(MethodCallExpr n, Object arg) {
        super.visit(n, arg);
        if (n.getScope() == null || n.getScope().toString().equals("this")) {
            //in case the method is a method from the current class
            //first add a link to the method declaration
            util.addLinkToMethodDeclaration(n);
        } else {
            //         util.addLinkToExternalMethodDeclaration(n);
        }
    }

    @Override
    public void visit(ReturnStmt n, Object arg) {
        super.visit(n, arg);
        try {
            if (n.getExpr() instanceof NameExpr) {
                NameExpr name = (NameExpr) n.getExpr();
                util.addLinkToVariableDeclaration(name.getBeginLine(), name.getBeginColumn(), name.getName(), n);
            }
        } catch (NullPointerException exs) {
            return;
        }
    }

    @Override
    public void visit(ArrayAccessExpr n, Object arg) {
        super.visit(n, arg);
        int startLine = n.getBeginLine();
        if (n.getName() instanceof NameExpr) {
            util.addLinkToVariableDeclaration(startLine, n.getName().getBeginColumn(), n.getName().toString(), n);
        }
        if (n.getIndex() instanceof NameExpr) {
            util.addLinkToVariableDeclaration(startLine, n.getIndex().getBeginColumn(), n.getIndex().toString(), n);
        }
    }

    @Override
    public void visit(AssignExpr n, Object arg) {
        super.visit(n, arg);
        if (n.getTarget() instanceof NameExpr) {
            NameExpr target = (NameExpr) n.getTarget();
            util.addLinkToVariableDeclaration(target.getBeginLine(), target.getBeginColumn(), target.getName(), n);
        }
        if (n.getValue() instanceof NameExpr) {
            NameExpr value = (NameExpr) n.getValue();
            util.addLinkToVariableDeclaration(value.getBeginLine(), value.getBeginColumn(), value.getName(), n);
        }
    }

    @Override
    public void visit(FieldAccessExpr n, Object arg) {
        super.visit(n, arg);
        if (n.getScope().toString().equals("this")) {
            util.addLinkToVariableDeclaration(n.getBeginLine(), n.getBeginColumn() + 5, n.toString(), n);
        }
        //  util.addLinkToExternalVariableDeclaration(n.getBeginLine(), n.getBeginColumn(), n.getField(), n, n.getScope().toString());
    }

    @Override
    public void visit(BinaryExpr n, Object arg) {
        super.visit(n, arg);
        if (n.getLeft() instanceof NameExpr) {
            util.addLinkToVariableDeclaration(n.getLeft().getBeginLine(), n.getLeft().getBeginColumn(), n.getLeft().toString(), n);
        }
        if (n.getRight() instanceof NameExpr) {
            util.addLinkToVariableDeclaration(n.getRight().getBeginLine(), n.getRight().getBeginColumn(), n.getRight().toString(), n);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
        if (util.getVisibilityFromModifier(n.getModifiers()) == Visibility.PUBLIC) {
            this.typeDeclarations.add(this.packageName + "." + n.getName());
        }
    }

    public List<String> getTypeDeclarations() {
        return typeDeclarations;
    }

    public List<String> getImports() {
        return imports;
    }
}
