package org.codesearch.commons.plugins.javacodeanalyzerplugin;

import japa.parser.ast.Node;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.StringLiteralExpr;

import java.util.LinkedList;
import java.util.List;

import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Visibility;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ClassNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ExternalClassOrEnumUsage;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ExternalMethodUsage;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ExternalVariableOrEnumValueUsage;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.FileNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.MethodNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.VariableNode;

/**
 * Provides several methods needed for Java CodeAnalysis
 * @author David Froehlich
 */
public class AnalyzerUtil {

    /** the fileNode that was parsed during analysis */
    private FileNode fileNode;
    /** the list of all usages that are created with the util methods */
    private List<Usage> usages = new LinkedList<Usage>();

    public List<Usage> getUsages() {
        return usages;
    }

    /**
     * creates a new instance of AnalyzerUtil with the given FileNode as foundation for all methods
     * @param fileNode
     */
    public AnalyzerUtil(FileNode fileNode) {
        this.fileNode = fileNode;
    }

    /**
     * adds a usage to the usages list if a reference variable is found by getVariableDeclarationForUsage
     * @param lineNumber the lineNumber of the usage
     * @param startColumn the startColumn of the usage
     * @param varName the name of the variable
     * @param parent the parent element that contains the usage
     */
    public boolean addLinkToVariableDeclaration(int lineNumber, int startColumn, String varName, Node parent) {
        VariableNode refVar = getVariableDeclarationForUsage(lineNumber, varName, parent);
        if (refVar == null) {
            return false;
        }
        usages.add(new Usage(startColumn, lineNumber, refVar.getName().length(), refVar.getStartLine(), refVar.getName()));
        return true;
    }

    /**
     * adds a link to the external links list
     * @param lineNumber the lineNumber of the usage
     * @param startColumn the startColumn of the usage
     * @param className the name of the class that is used
     */
    public void addLinkToExternalClassDeclaration(int lineNumber, int startColumn, String className) {
        usages.add(new ExternalClassOrEnumUsage(startColumn, lineNumber, className.length(), className, className));
    }

    /**
     * adds a link to the external links list
     * @param lineNumber the lineNumber of the usage
     * @param startColumn the startColumn of the usage
     * @param varName the name of the variable
     * @param parent the parent element that contains the usage
     * @param className the class that contains the variable that is used
     */
    public void addLinkToExternalVariableDeclaration(int lineNumber, int startColumn, String varName, Node parent, String className) {
        //add a link to the variable
        usages.add(new ExternalVariableOrEnumValueUsage(startColumn, lineNumber, className.length(), varName, className));
    }

    /**
     * adds a usage to the usage list
     * @param n the MethodCallExpr containing the information about the usage
     */
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

    /**
     * adds a link to the external link list
     * @param n the MethodCallExpr that contains the information of the usage
     */
    public void addLinkToExternalMethodDeclaration(MethodCallExpr n) {
        int lineNumber = n.getBeginLine();
        String methodName = n.getName();
        String scopeName = n.getScope().toString();
        int methodCallColumn = n.getBeginColumn() + scopeName.length() + 1;
        String className = null;
        List<String> paramTypes = new LinkedList<String>();
        int scopeColumn = n.getBeginColumn();
        VariableNode scopeObject = getVariableDeclarationForUsage(n.getBeginLine(), scopeName, n);
        if (scopeObject == null) { //the method is a static method from another class, and is not simply the return type of some other methods method call
            className = scopeName;
            if (!scopeName.contains(".")) {
                usages.add(new ExternalClassOrEnumUsage(scopeColumn, lineNumber, className.length(), className, className));
            }
        } else { //the method is called from an object in the class
            className = scopeObject.getType();
            //       usages.add(new Usage(n.getBeginColumn(), lineNumber, scopeName.length(), scopeObject.getStartLine(), scopeName));
        }
        if (n.getArgs() != null) {
            for (Expression ex : n.getArgs()) {
                String paramType = getTypeOfParameter(ex, n);
                paramTypes.add(paramType);
            }
        }
        if (!className.contains(".")) {
            if (n.getBeginLine() == n.getEndLine()) {
                usages.add(new ExternalMethodUsage(methodCallColumn, lineNumber, methodName.length(), methodName, className, paramTypes));
            }
        }
    }

    /**
     * adds a link to the scope of the field (either an object in the current class or a class of which a static variable is referenced)
     * adds a link to the field itself
     * @param ex the FieldAccessExpr
     * @param parent the parentNode of the field access
     */
    public void addLinkToExternalField(FieldAccessExpr ex, Node parent) {
        String scope = ex.getScope().toString();
        int startColumn = ex.getBeginColumn() + 1;
        String className;
        if (!addLinkToVariableDeclaration(ex.getBeginLine(), ex.getBeginColumn(), scope, parent)) {
            addLinkToExternalClassDeclaration(ex.getBeginLine(), ex.getBeginColumn(), ex.getScope().toString());
            startColumn += scope.length();
            className = scope;
        } else {
            VariableNode refVar = getVariableDeclarationForUsage(ex.getBeginLine(), scope, parent);
            className = refVar.getType();
            startColumn += refVar.getName().length();
        }
        addLinkToExternalVariableDeclaration(ex.getBeginLine(), startColumn, ex.getField(), parent, className);
    }

    /**
     * returns the type of the parameter so it can be compared to variable types
     * @param ex the expression containing the parameter
     * @param parent the parent node containing the expression
     * @return the type of the parameter
     */
    private String getTypeOfParameter(Expression ex, Node parent) {
        String paramType = null;
        if (ex instanceof NameExpr) {
            VariableNode currentParam = getVariableDeclarationForUsage(parent.getBeginLine(), ex.toString(), parent);
            try {
                paramType = currentParam.getType();
            } catch (NullPointerException exc) {
                return null;
            }
        } else if (ex instanceof MethodCallExpr) {
            MethodCallExpr methodParamExpr = (MethodCallExpr) ex;
            MethodNode methodParam = getMethodDeclarationForUsage(methodParamExpr.getName(), methodParamExpr.getArgs(), ex);
            try {
                paramType = methodParam.getReturnType();
            } catch (NullPointerException exc) {
                return null;
            }
        } else if (ex instanceof StringLiteralExpr) {
            paramType = "String";
        } else if (ex instanceof FieldAccessExpr) {
            paramType = null;
        } else if (ex instanceof BooleanLiteralExpr) {
            paramType = "boolean";
        } else if (ex instanceof CharLiteralExpr) {
            paramType = "char";
        } else if (ex instanceof IntegerLiteralExpr) {
            paramType = "int";
        } else if (ex instanceof LongLiteralExpr) {
            paramType = "long";
        } else if (ex instanceof DoubleLiteralExpr) {
            paramType = "double";
        }
        return paramType;
    }

    /**
     * returns the method that is valid at the lineNumber
     * @param lineNumber
     * @return the method
     */
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

    /**
     * gets the MethodNode of the declaration of the usage
     * is not guaranteed to work, since external types or polymorphed types can not be recognized
     * @param methodName the name of the method
     * @param params all parameters as expressions
     * @param parent the parent node of the method call
     * @return the MethodNode that is called
     */
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
                                if (variableNodeType != null && method.getParameters().get(i).getName().equals(variableNodeType)) {
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

    /**
     * returns the VariableNode of the declaration of the used variable
     * @param lineNumber the lineNumber of the usage
     * @param name the name of the variable
     * @param parent the parent node of the usage
     * @return the VariableNode of the declaration of the variable
     */
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
            for (ClassNode clazz : fileNode.getClasses()) {
                for (VariableNode var : clazz.getAttributes()) {
                    if (var.getName().equals(name)) {
                        return var;
                    }
                }
            }

        } catch (NullPointerException ex) {
            return null;
        }
        return null;
    }

    /**
     * checks whether the variable is valid within a certain node, so a local variable is only valid in the node it is declared in and in all child nodes
     * @param node
     * @param variable
     * @return whether the variable is valid
     */
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

    /**
     * returns the class that is valid at the lineNumber
     * so if the lineNumber is of an internal class within another class the inner class will be returned
     * @param lineNumber
     * @return the class that is valid at the line
     */
    public ClassNode getClassAtLine(int lineNumber) {
        ClassNode classNode = null;
        for (ClassNode clazz : fileNode.getClasses()) {
            if (classNode == null || (clazz.getStartLine() > classNode.getStartLine() && clazz.getStartLine() < lineNumber && clazz.getStartLine() + clazz.getNodeLength() > lineNumber)) {
                classNode = clazz;
            }
        }
        return classNode;
    }

    /**
     * parses the modifier from the JavaParser to a visibility
     * @param modifier the modifier the JavaParser has determined
     * @return the visibility
     */
    public Visibility getVisibilityFromModifier(int modifier) {
        String modifierString = Integer.toBinaryString(modifier);
        if (!modifierString.equals("0")) {
            if (modifierString.charAt(modifierString.length() - 1) == '1') {
                return Visibility.PUBLIC;
            }
            if (modifierString.charAt(modifierString.length() - 2) == '1') {
                return Visibility.PRIVATE;
            }
            if (modifierString.charAt(modifierString.length() - 3) == '1') {
                return Visibility.PROTECTED;
            }
        }
        return Visibility.DEFAULT;
    }
}
