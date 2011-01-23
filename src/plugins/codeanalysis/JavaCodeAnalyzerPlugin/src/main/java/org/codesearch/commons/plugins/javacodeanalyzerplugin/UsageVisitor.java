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
package org.codesearch.commons.plugins.javacodeanalyzerplugin;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.LinkedList;
import java.util.List;

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

    /**
     * creates a new instance of UsageVisitor
     * @param util the AnalyzerUtil class that will be used for this visitor
     * @param packageName the package name of the currently analyzed file
     */
    public UsageVisitor(AnalyzerUtil util, String packageName) {
        this.util = util;
        this.packageName = packageName;
    }

    /**
     * adds an entry to the import list
     * @param n the ImportDeclaration that is added
     * @param arg null
     */
    @Override
    public void visit(ImportDeclaration n, Object arg) {
        super.visit(n, arg);
        String importName = n.getName().toString();
        if (n.isAsterisk()) {
            importName += ".*";
        }
        imports.add(importName);
    }

    /**
     * checks the parameters of the invocation if they are NameExpr
     * if so, adds a usage for the variable used as a parameter
     * @param n the ExplicitConstructorInvocationStmt
     * @param arg the parent Node of the Stmt
     */
    @Override
    public void visit(ExplicitConstructorInvocationStmt n, Object arg) {
        super.visit(n, arg);
        if (n.getArgs() != null) {
            for (Expression expr : n.getArgs()) {
                if (expr instanceof NameExpr) {
                    util.addLinkToVariableDeclaration(expr.getBeginLine(), expr.getBeginColumn(), expr.toString(), n);
                }
            }
        }

    }

    /**
     * adds a link from the MethodCall to the MethodNode and from the parameters to the VariableNodes
     * @param n the MethodCallExpr
     * @param arg the parent node of the MethodCallExpr
     */
    @Override
    public void visit(MethodCallExpr n, Object arg) {
        super.visit(n, arg);
        List<Expression> parameterList = n.getArgs();
        if (parameterList != null) {
            for (Expression currentParameter : parameterList) {
                if (currentParameter instanceof NameExpr) {
                    util.addLinkToVariableDeclaration(currentParameter.getBeginLine(), currentParameter.getBeginColumn(), currentParameter.toString(), n);
                }
            }
        }
        String currentClassName = util.getClassAtLine(n.getBeginColumn()).getName();
        if (n.getScope() == null || n.getScope().toString().equals("this") || n.getScope().toString().equals(currentClassName)) {
            //in case the method is a method from the current class
            //first add a link to the method declaration
            util.addLinkToMethodDeclaration(n);
        } else {
            util.addLinkToVariableDeclaration(n.getBeginLine(), n.getBeginColumn(), n.getScope().toString(), n);
            util.addLinkToExternalMethodDeclaration(n);
        }
    }

    /**
     * visits the ReturnStmt and in case the expression is a NameExpr adds a link to the variable
     * @param n the ReturnStmt
     * @param arg the parent node of the statement
     */
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

    /**
     * visits the ArrayAccessExpr and in case either the name or the index is a NameExpr adds a link to the variable
     * @param n the ArrayAccessExpr
     * @param arg the parent node of the expression
     */
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

    /**
     * visits the ClassOrInteraceType and adds an ExternalUsage to the class declaration
     * @param n the ClassOrInterfaceType
     * @param arg the parent node of the type
     */
    @Override
    public void visit(ClassOrInterfaceType n, Object arg) {
        super.visit(n, arg);
        util.addLinkToExternalClassDeclaration(n.getBeginLine(), n.getBeginColumn(), n.getName());
    }

    /**
     * visits the ObjectCreationExpression and in case one of the arguments is a NameExpr adds a link to the variable
     * @param n the ObjectCreationExpr
     * @param arg the parent node of the expression
     */
    @Override
    public void visit(ObjectCreationExpr n, Object arg) {
        super.visit(n, arg);
        if (n.getArgs() != null) {
            for (Expression expr : n.getArgs()) {
                if (expr instanceof NameExpr) {
                    util.addLinkToVariableDeclaration(expr.getBeginLine(), expr.getBeginColumn(), expr.toString(), n);
                }
            }
        }
    }

    /**
     * visits the AssinExpr and in case either the target or the value is a NameExpr adds a link to the variable
     * @param n the AssignExpr
     * @param arg the parent node of the expression
     */
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

    /**
     * visits the UnaryExpr and in case the expression is a NameExpr adds a link to the variable
     * @param n the UnaryExpr
     * @param arg the parent node of the expression
     */
    @Override
    public void visit(UnaryExpr n, Object arg) {
        super.visit(n, arg);
        if (n.getExpr() instanceof NameExpr) {
            NameExpr expr = (NameExpr) n.getExpr();
            util.addLinkToVariableDeclaration(expr.getBeginLine(), expr.getBeginColumn(), expr.toString(), n);
        }
    }

    @Override
    public void visit(IfStmt n, Object arg) {
        super.visit(n, arg);
        if(n.getCondition() instanceof NameExpr){
            util.addLinkToVariableDeclaration(n.getCondition().getBeginLine(), n.getCondition().getBeginColumn(), n.getCondition().toString(), n);
        }
    }

    /**
     * visits the ConditinoalExpr and in case the condition, the thenExpression or the elseExpression is a NameExpr adds a link to the variable
     * @param n the ContitionalExpr
     * @param arg the parent node of the statement
     */
    @Override
    public void visit(ConditionalExpr n, Object arg) {
        super.visit(n, arg);
        Expression conditionExpr = n.getCondition();
        if (conditionExpr instanceof NameExpr) {
            String varName = conditionExpr.toString();
            util.addLinkToVariableDeclaration(conditionExpr.getBeginLine(), conditionExpr.getBeginColumn(), varName, conditionExpr);
        }
        Expression thenExpr = n.getThenExpr();
        if (thenExpr instanceof NameExpr) {
            String varName = thenExpr.toString();
            util.addLinkToVariableDeclaration(thenExpr.getBeginLine(), thenExpr.getBeginColumn(), varName, n);
        }
        Expression elseExpr = n.getElseExpr();
        if (elseExpr instanceof NameExpr) {
            String varName = elseExpr.toString();
            util.addLinkToVariableDeclaration(elseExpr.getBeginLine(), elseExpr.getBeginColumn(), varName, n);
        }
    }

    /**
     * visits the FieldAccessExpr and in case the scope is a NameExpr adds a link to the variable
     * if the variable is in another file adds an ExternalUsage
     * @param n the FieldAccessExpr
     * @param arg the parent node of the expression
     */
    @Override
    public void visit(FieldAccessExpr n, Object arg) {
        super.visit(n, arg);
        if (n.getScope().toString().equals("this")) {
            util.addLinkToVariableDeclaration(n.getBeginLine(), n.getBeginColumn() + 5, n.toString(), n);
        } else {
            util.addLinkToExternalField(n, (Node) n.getData());
        }
    }

    /**
     * visits the BinaryExpr and in case the left or the right expression is a NameExpr adds a link to the variable
     * @param n the BinaryExpr
     * @param arg the parent node of the expression
     */
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
    public void visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
        if (n.getThrows() != null) {
            for (NameExpr currentThrow : n.getThrows()) {
                util.addLinkToExternalClassDeclaration(currentThrow.getBeginLine(), currentThrow.getBeginColumn(), currentThrow.getName());
            }
        }
    }

    /**
     * visits the ClassOrInterfaceDeclaration and adds the type to the typeDeclarations of the file
     * @param n the ClassOrInterfaceDeclaration
     * @param arg the parent node of the declaration
     */
    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
        // if (util.getVisibilityFromModifier(n.getModifiers()) == Visibility.PUBLIC) {
        this.typeDeclarations.add(this.packageName + "." + n.getName()); //FIXME
        // }
    }

    public List<String> getTypeDeclarations() {
        return typeDeclarations;
    }

    public List<String> getImports() {
        imports.add(packageName + ".*");
        return imports;
    }
}
