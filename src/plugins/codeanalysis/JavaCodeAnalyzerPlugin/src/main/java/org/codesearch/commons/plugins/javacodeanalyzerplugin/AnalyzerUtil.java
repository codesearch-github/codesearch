/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.javacodeanalyzerplugin;

import com.mysql.jdbc.NotImplemented;
import japa.parser.ast.Node;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.NotImplementedException;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalLink;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalMethodLink;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalVariableLink;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Visibility;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ClassNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.FileNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.MethodNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.VariableNode;

/**
 * Provides several methods needed for Java CodeAnalysis
 * @author David Froehlich
 */
public class AnalyzerUtil {

    private FileNode fileNode;
    private List<Usage> usages = new LinkedList<Usage>();
    private List<ExternalLink> externalLinks = new LinkedList<ExternalLink>();

    public List<ExternalLink> getExternalLinks() {
        return externalLinks;
    }

    public List<Usage> getUsages() {
        return usages;
    }

    public AnalyzerUtil(FileNode fileNode) {
        this.fileNode = fileNode;
    }

    public void addLinkToVariableDeclaration(int lineNumber, int startColumn, String varName, Node parent) {
        VariableNode refVar = getVariableDeclarationForUsage(lineNumber, varName, parent);
        if (refVar == null) {
            //System.out.println("variableName " + varName);
            return;
        }
        usages.add(new Usage(startColumn, lineNumber, refVar.getName().length(), refVar.getStartLine(), refVar.getName()));
    }

    public void addLinkToExternalVariableDeclaration(int lineNumber, int startColumn, String varName, Node parent, String className) {
        //add a link to the variable
        this.externalLinks.add(new ExternalVariableLink(lineNumber, startColumn + className.length() + 1, varName.length(), className, varName));
        //add link for the class
        this.externalLinks.add(new ExternalLink(lineNumber, startColumn, className.length(), className));
    }

    public void addLinkToExternalMethodDeclaration(MethodCallExpr n) {
        int lineNumber = n.getBeginLine();
        String methodName = n.getName();
        String scopeName = n.getScope().toString();
        int methodCallColumn = n.getBeginColumn() + scopeName.length() + 1;
        String className = null;
        List<String> paramTypes = new LinkedList<String>();
        int scopeColumn = n.getBeginColumn();
        VariableNode scopeObject = getVariableDeclarationForUsage(n.getBeginLine(), scopeName, n);
        if (scopeObject == null) { //the method is a static method from another class
            className = scopeName;
            externalLinks.add(new ExternalLink(lineNumber, scopeColumn, scopeName.length(), className));
        } else { //the method is called from an object in the class
            className = scopeObject.getType();
            usages.add(new Usage(n.getBeginColumn(), lineNumber, scopeName.length(), scopeObject.getStartLine(), scopeName));
        }
        if (n.getArgs() != null) {
            for (Expression ex : n.getArgs()) {
                String paramType = getTypeOfParameter(ex, n);
                paramTypes.add(paramType);
            }
        }
        ExternalMethodLink methodLink = new ExternalMethodLink(lineNumber, methodCallColumn, methodName.length(), className, methodName, paramTypes);
        externalLinks.add(methodLink);
    }

    private String getTypeOfParameter(Expression ex, Node parent) {
        String paramType = null;
        if (ex instanceof NameExpr) {
//            if(ex.toString().equals("locationId")){
//                ex.getBeginColumn();
//            }
            VariableNode currentParam = getVariableDeclarationForUsage(parent.getBeginLine(), ex.toString(), parent);
            try {
                paramType = currentParam.getType();
            } catch (NullPointerException exc) {
                return "";
            }
        } else if (ex instanceof MethodCallExpr) {
            MethodCallExpr methodParamExpr = (MethodCallExpr) ex;
            MethodNode methodParam = getMethodDeclarationForUsage(methodParamExpr.getName(), methodParamExpr.getArgs(), ex);
            try {
                paramType = methodParam.getReturnType();
            } catch (NullPointerException exc) {
                return ""; //FIXME
            }
        } else if (ex instanceof StringLiteralExpr) {
            paramType = "String";
        } else if (ex instanceof FieldAccessExpr) {
            paramType = "String"; //TODO figure out a way to replace this
        }
        return paramType;
    }

    public void addLinkToMethodDeclaration(MethodCallExpr n) {
        String methodName = n.getName();
        List<Expression> parameterList = n.getArgs();
        int startColumn = n.getBeginColumn();
        int startLine = n.getBeginLine();
        int length = methodName.length();
        MethodNode methodNode = getMethodDeclarationForUsage(methodName, parameterList, n);
        if (methodNode != null) {
            int referenceLine = methodNode.getStartLine();
            usages.add(new Usage(startColumn, startLine, length, referenceLine, methodName));
        } else {
            //    throw new NotImplementedException(); //FIXME
        }

    }

    public MethodNode getMethodAtLine(int lineNumber) {
        MethodNode method = null;
        for (ClassNode clazz : fileNode.getClasses()) {
            for (MethodNode currentMethod : clazz.getMethods()) {
                if (lineNumber > currentMethod.getStartLine()) {
                    if ((method == null) || (method.getStartLine() < currentMethod.getStartLine() && currentMethod.getStartLine() < lineNumber)) {
                        method = (MethodNode) currentMethod;
                    }
                }
            }
        }
        return method;
    }

    public MethodNode getMethodDeclarationForUsage(String methodName, List<Expression> params, Node parent) { //TODO add parameter check
        int paramCount = params == null ? 0 : params.size();
        MethodNode foundMethod = null;
        for (ClassNode clazz : fileNode.getClasses()) {
            for (MethodNode method : clazz.getMethods()) {
                if (methodName.equals(method.getName()) && paramCount == method.getParameters().size()) {
                    if (foundMethod == null) {
                        foundMethod = method;
                    } else {
                        //in case there are multiple methods with the same name and the same number of parameters a parameter check has to be performed
                        for (int i = 0; i < paramCount; i++) {
                            try {
                                String variableNodeType = getTypeOfParameter(params.get(i), parent);

                                //If one of the parameters does not match in type, the previously set method (foundMethod) is the correct one, otherwise the new method (method)
                                if (method.getParameters().get(i).getName().equals(variableNodeType)) {
                                    return foundMethod;
                                }
                            } catch (NullPointerException ex) {
                                return null;
                                //TODO find out why this occurs 
                            }
                        }
                        return method;
                    }
                }
            }
        }
        if (foundMethod != null) {
            return foundMethod;
        }
        return null;
    }

    public VariableNode getVariableDeclarationForUsage(int lineNumber, String name, Node parent) {
        try {
            String[] nameParts = name.split("\\.");
            String scope = null;
            if (nameParts.length == 2) {
                scope = nameParts[0];
                name = nameParts[1];
            }
            if (scope == null) {
                MethodNode method = getMethodAtLine(lineNumber);
                if (method != null) {
                    for (VariableNode currentVariable : method.getLocalVariables()) {
                        if (currentVariable.getName().equals(name)) {
                            if (variableIsValidWithinNode(parent, currentVariable)) {
                                return currentVariable;
                            }
                        }
                    }
                    for (VariableNode currentVariable : method.getParameters()) {
                        if (currentVariable.getName().equals(name)) {
                            return currentVariable;
                        }
                    }
                }
                //in case no variable is found an attribute with the correct name will be searched (after the other else blocks)
            }
            ClassNode clazz = getClassAtLine(lineNumber);
            for (VariableNode var : clazz.getAttributes()) {
                if (var.getName().equals(name)) {
                    return var;
                }
            }
        } catch (NullPointerException ex) {
            return null;
        }
        return null;
    }

    public boolean variableIsValidWithinNode(Node node, VariableNode variable) {
        int variableParentLine = variable.getParentLineDeclaration();
        Node parent = node;
        do {
            if (parent.getBeginLine() == variableParentLine) {
                return true;
            }
            parent = (Node) parent.getData();
        } while (parent != null);
        return false;
    }

    public ClassNode getClassAtLine(int lineNumber) {
        ClassNode classNode = null;
        for (ClassNode clazz : fileNode.getClasses()) {
            if (classNode == null || (clazz.getStartLine() > classNode.getStartLine() && clazz.getStartLine() < lineNumber)) {
                classNode = clazz;
            }
        }
        return classNode;
    }

    public Visibility getVisibilityFromModifier(int modifier) {
        Visibility visibility;
        switch (modifier) {
            case 0:
                visibility = Visibility.DEFAULT;
                break;
            case 1:
                visibility = Visibility.PUBLIC;
                break;
            case 2:
                visibility = Visibility.PRIVATE;
                break;
            default:
                visibility = Visibility.DEFAULT;
            //TODO implement other cases
        }
        return visibility;
    }
}
