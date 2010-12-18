/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.javacodeanalyzerplugin;

import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.MethodNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ClassNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.FileNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.VariableNode;

/**
 * a visitor used to visit the AST created by the JavaParser
 * extracts all important information and creates a simplified AST
 * @author David Froehlich
 */
//TODO rename
public class Visitor extends VoidVisitorAdapter {

    private FileNode fileNode;
    private Map<Integer, Usage> usages;

    public Visitor(FileNode fileNode, Map<Integer, Usage> usages) {
        this.fileNode = fileNode;
        this.usages = usages;
    }

    @Override
    public void visit(FieldAccessExpr n, Object arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(FieldDeclaration n, Object arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(TypeDeclarationStmt n, Object arg) {
        ClassNode clazz = new ClassNode();
        int startLine = n.getBeginLine();
        //    int endLine = n.getEndLine();
        String clazzName = n.getTypeDeclaration().getName();
        int nodeLength = n.getEndColumn() - n.getBeginColumn();
        clazz.setName(clazzName);
        clazz.setStartLine(startLine);
        clazz.setStartPosition(startLine);
        clazz.setNodeLength(nodeLength);
        fileNode.getClasses().add(clazz);
        super.visit(n, arg);
    }

//    @Override
//    public void visit(MethodCallExpr n, Object arg) {
//        super.visit(n, arg);
//        System.out.println("Method " + n.getName() + " is called in line " + n.getBeginLine());
//    }
    @Override
    public void visit(MethodDeclaration n, Object arg) {
        MethodNode method = new MethodNode();
        String methodName = n.getName();
        int startLine = n.getBeginLine();
        int nodeLength = n.getEndColumn() - n.getBeginColumn();
        method.setName(methodName);
        method.setStartLine(startLine);
        method.setNodeLength(nodeLength);
        List<VariableNode> parameters = new LinkedList<VariableNode>();
        try {
            for (Parameter param : n.getParameters()) {
                VariableNode newParam = new VariableNode();
                String name = param.toString(); //FIXME
                newParam.setName(name);
                parameters.add(newParam);
            }
        } catch (NullPointerException ex) {
        }
        method.setParameters(parameters);
        getMostInnerClassForLine(startLine).getMethods().add(method);
        super.visit(n, arg);
    }

    /**
     * finds the appropriate type for the line number, so if for instance a type is created within a type and the method is called for a line in the subclass, the subclass is returned
     * @param startLine the line for which the most inner class is searched
     * @return the most inner class
     */
    private ClassNode getMostInnerClassForLine(int startLine) {
        ClassNode mostInnerClass = null;
        for (ClassNode clazz : this.fileNode.getClasses()) {
            if (clazz.getStartLine() < startLine && ((mostInnerClass == null) || (clazz.getStartLine() > mostInnerClass.getStartLine()))) {
                mostInnerClass = clazz;
            }
        }
        return mostInnerClass;
    }
}
