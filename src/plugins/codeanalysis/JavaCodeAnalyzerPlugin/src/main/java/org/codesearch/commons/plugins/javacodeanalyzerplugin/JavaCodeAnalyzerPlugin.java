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

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.WhileStmt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.commons.database.DatabaseEntryNotFoundException;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Visibility;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ClassNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.EnumNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.EnumValueNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ExternalClassOrEnumUsage;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ExternalMethodUsage;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ExternalVariableOrEnumValueUsage;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.FileNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.MethodNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.VariableNode;

/**
 *
 * @author David Froehlich
 */
public class JavaCodeAnalyzerPlugin implements CodeAnalyzerPlugin {

    private FileNode fileNode = new FileNode();
    private List<Usage> usages;
    private String fileContent;
    private List<String> typeDeclarations;
    private List<String> imports;
    private AnalyzerUtil util;

    /**
     * sets the lineNumber and the targetFilePath for the class usage
     * @param usage the usage that is parsed
     * @param repository the repository holding the files
     * @param fileImports the imports in the file that holds the usage
     * @param originFilePath the filePath of the file that holds the usage
     * @throws DatabaseAccessException
     */
    public void parseLineNumberAndFileNameOfUsage(ExternalClassOrEnumUsage usage, String repository, List<String> fileImports, String originFilePath) throws DatabaseAccessException {
        String className = usage.getTargetClassName();
        String targetFilePath = getFilePathOfDeclaration(repository, fileImports, className);
        usage.setTargetFilePath(targetFilePath);
        if (targetFilePath == null) {
            return;
        }

        usage.setReferenceLine(-1);
    }

    /**
     * sets the lineNumber and the targetFilePath for the variable usage
     * @param usage the usage that is parsed
     * @param repository the repository holding the files
     * @param fileImports the imports in the file that holds the usage
     * @param originFilePath the filePath of the file that holds the usage
     * @throws DatabaseAccessException
     */
    @Deprecated
    public void parseLineNumberAndFileNameOfUsage(ExternalVariableOrEnumValueUsage usage, String repository, List<String> fileImports, String originFilePath) throws DatabaseAccessException, DatabaseEntryNotFoundException {
//        String className = usage.getTargetClassName();
//        String targetFilePath = getFilePathOfDeclaration(repository, fileImports, className);
//        usage.setTargetFilePath(targetFilePath);
//        if (targetFilePath == null) {
//            return;
//        }
//        AstNode currentFileNode = DBAccess.getBinaryIndexForFile(targetFilePath, repository);
//        checkChildNodesForVariable(currentFileNode, usage);
    }


    /**
     * recursively checks all child nodes of this node for the values of the usage
     * if it finds the declarations sets the referenceLineNumber of the usage to the start line of the declaration
     * @param node
     * @param usage
     */
    @Deprecated
    private void checkChildNodesForVariable(AstNode node, ExternalVariableOrEnumValueUsage usage) {
        for (AstNode currentNode : node.getChildNodes()) {
            if (currentNode instanceof VariableNode) {
                VariableNode currentVariable = (VariableNode) currentNode;
                if (currentVariable.getName().equals(usage.getReplacedString()) && currentVariable.getVisibility() != Visibility.DEFAULT) { //FIXME
                    usage.setReferenceLine(currentVariable.getStartLine());
                    return;
                }
            }
            checkChildNodesForVariable(currentNode, usage);
        }
    }

    /**
     * sets the lineNumber and the targetFilePath for the method usage
     * @param usage the usage that is parsed
     * @param repository the repository holding the files
     * @param fileImports the imports in the file that holds the usage
     * @param originFilePath the filePath of the file that holds the usage
     * @throws DatabaseAccessException
     */
    @Deprecated
    public void parseLineNumberAndFileNameOfUsage(ExternalMethodUsage usage, String repository, List<String> fileImports, String originFilePath) throws DatabaseAccessException, DatabaseEntryNotFoundException {
//        String className = usage.getTargetClassName();
//        String targetFilePath = getFilePathOfDeclaration(repository, fileImports, className);
//        usage.setTargetFilePath(targetFilePath);
//        if (targetFilePath == null) {
//            return;
//        }
//        AstNode currentFileNode = DBAccess.getBinaryIndexForFile(originFilePath, repository);
//        MethodNode bestMatch = null;
//        int paramCount = usage.getParameters().size();
//        outer:
//        for (AstNode currentNode : currentFileNode.getChildNodes()) {
//            if (currentNode instanceof MethodNode) {
//                MethodNode currentMethodNode = (MethodNode) currentNode;
//                if (currentNode.getName().equals(usage.getReplacedString()) && currentMethodNode.getParameters().size() == usage.getParameters().size()) {
//                    if (bestMatch == null) {
//                        bestMatch = currentMethodNode;
//                    } else {
//                        for (int i = 0; i < paramCount; i++) {
//                            String givenParam = usage.getParameters().get(i);
//                            String currentNodeParam = currentMethodNode.getParameters().get(i).getType();
//                            if (!givenParam.equals(currentNodeParam)) {
//                                continue outer;
//                            }
//                        }
//                        bestMatch = currentMethodNode;
//                    }
//                }
//            }
//        }
//        if (bestMatch != null) {
//            usage.setReferenceLine(bestMatch.getStartLine());
//        }
//        return;
    }

    /**
     * returns the file path of the file declaring the given class
     * @param repository the repository holding the file
     * @param fileImports the list of imports in the source file, these will be searched through
     * @param className the name of the class that is searched
     * @return the path of the file holding the declaration
     * @throws DatabaseAccessException
     */
    @Deprecated
    private String getFilePathOfDeclaration(String repository, List<String> fileImports, String className) throws DatabaseAccessException {
//        String targetFilePath;
//        List<String> asteriskImports = new LinkedList<String>();
//        boolean importMatch = false;
//        for (String currentImport : fileImports) {
//            if (currentImport.endsWith("." + className)) {
//                className = currentImport;
//                importMatch = true;
//            } else if (currentImport.endsWith("*")) {
//                asteriskImports.add(currentImport);
//            }
//        }
//        if (!importMatch) {
//            targetFilePath = DBAccess.getFilePathForTypeDeclaration(className, repository, asteriskImports);
//        } else {
//            targetFilePath = DBAccess.getFilePathForTypeDeclaration(className, repository);
//        }
//      return targetFilePath;

        return null;
    }

    /** {@inheritDoc} */
    public FileNode getFileNode() {
        return fileNode;
    }

    /** {@inheritDoc} */
    @Override
    public void analyzeFile(final String fileContent) throws CodeAnalyzerPluginException {
        CompilationUnit cu = null;
        fileNode = new FileNode();
        util = new AnalyzerUtil(fileNode);
        this.fileContent = fileContent;
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(fileContent.getBytes()); //TODO maybe change specification of analyzeFile to take an inputStream as parameter
            cu = JavaParser.parse(bais);
            buildAST(cu);
            //parse usages via UsageVisitor
            String packageName = null;
            try {
                packageName = cu.getPackage().getName().toString();
            } catch (NullPointerException ex) {
                //in case the class has no package
            }

            UsageVisitor uv = new UsageVisitor(util, packageName);
            cu.accept(uv, null);
            usages = util.getUsages();
            typeDeclarations = uv.getTypeDeclarations();
            imports = uv.getImports();
            Collections.sort(usages);

        } catch (ParseException ex) {
            throw new CodeAnalyzerPluginException(ex.getMessage());
        } finally {
            try {
                bais.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * parses all declarations from the CompilationUnit into the fileNode
     * @param cu
     */
    private void buildAST(CompilationUnit cu) {
        //iterate all types (classes) from the compilation unit
        for (TypeDeclaration type : cu.getTypes()) {
            if (type instanceof ClassOrInterfaceDeclaration) {
                parseContentOfClass(((ClassOrInterfaceDeclaration) type));
            } else if (type instanceof EnumDeclaration) {
                parseContentOfEnum(((EnumDeclaration)type), fileNode);
            }
        }

    }

    /**
     * parses the enum and adds it to the parent node
     * @param en
     */
    private void parseContentOfEnum(EnumDeclaration en, AstNode parent) {
        EnumNode enumNode = new EnumNode();
        enumNode.setName(en.getName());
        enumNode.setStartLine(en.getBeginLine());
        enumNode.setVisibility(util.getVisibilityFromModifier(en.getModifiers()));
        for(EnumConstantDeclaration ecd : en.getEntries()){
            EnumValueNode newValue = new EnumValueNode();
            newValue.setName(ecd.getName());
            newValue.setStartLine(ecd.getBeginLine());
            newValue.setStartPositionInLine(ecd.getBeginColumn());
            enumNode.getValues().add(newValue);
        }
        if(parent instanceof FileNode){
            ((FileNode)parent).getEnums().add(enumNode);
        } else if (parent instanceof ClassNode) {
            ((ClassNode)parent).getEnums().add(enumNode);
        }
    }

    /**
     * creates a new ClassNode and attaches it to the fileNode
     * parses all methods and statements in the class
     * @param type the TypeDeclaration node
     */
    private void parseContentOfClass(ClassOrInterfaceDeclaration type) {
        //create ClassNode and extract required info from TypeDeclaration
        ClassNode newClass = new ClassNode();
        int startLine = type.getBeginLine();
        //    int endLine = n.getEndLine();
        String clazzName = type.getName();
        int nodeLength = type.getEndLine() - type.getBeginLine();
        newClass.setName(clazzName);
        newClass.setStartLine(startLine);
        newClass.setStartPositionInLine(type.getBeginColumn());
        newClass.setNodeLength(nodeLength);
        fileNode.getClasses().add(newClass);
        //iterate all methods and attributes in class
        if (type.getMembers() != null) {
            for (BodyDeclaration member : type.getMembers()) {
                if (member instanceof ClassOrInterfaceDeclaration) {
                    parseContentOfClass((ClassOrInterfaceDeclaration) member);
                } else if (member instanceof MethodDeclaration) {
                    parseContentOfMethod(newClass, (MethodDeclaration) member);
                } else if (member instanceof ConstructorDeclaration) {
                    parseContentOfConstructor(newClass, (ConstructorDeclaration) member);
                } else if (member instanceof FieldDeclaration) {
                    FieldDeclaration fieldDeclaration = (FieldDeclaration) member;
                    for (VariableDeclarator v : fieldDeclaration.getVariables()) {
                        VariableNode newVariable = new VariableNode();

                        newVariable.setName(v.getId().getName());
                        newVariable.setType(fieldDeclaration.getType().toString());
                        newVariable.setStartLine(v.getBeginLine());
                        newVariable.setStartPositionInLine(v.getBeginColumn());
                        newVariable.setAttribute(true);
                        newClass.getAttributes().add(newVariable);
                    }
                } else if (member instanceof EnumDeclaration){
                    parseContentOfEnum((EnumDeclaration) member, newClass);
                }
            }
        }
    }

    /**
     * adds the method to the parentClass
     * parses all statements in the method
     * @param parentClass the class containing the method
     * @param method the MethodDeclaration node
     */
    private void parseContentOfMethod(ClassNode parentClass, MethodDeclaration method) {
        MethodNode newMethod = new MethodNode();
        String methodName = method.getName();
        String returnType = method.getType().toString();
        Visibility visibility = util.getVisibilityFromModifier(method.getModifiers());
        newMethod.setVisibility(visibility);
        newMethod.setName(methodName);
        newMethod.setReturnType(returnType);
        newMethod.setConstructor(false);
        newMethod.setStartPositionInLine(method.getBeginColumn());

        int startLine = method.getBeginLine();
        String stringUntilName = method.toString().substring(0, method.toString().indexOf(method.getName() + "("));
        int linesUntilName = stringUntilName.split("\n").length - 1;
        if (method.getJavaDoc() != null) {
            linesUntilName -= method.getJavaDoc().getEndLine() - method.getJavaDoc().getBeginLine() + 1;
        }
        startLine += linesUntilName;

        newMethod.setStartLine(startLine);

        //parse all parameters to VariableNodes
        try {
            for (Parameter param : method.getParameters()) {
                VariableNode newParam = new VariableNode();
                newParam.setStartLine(param.getBeginLine());
                String paramName = param.getId().getName();
                String paramType = param.getType().toString();
                newParam.setType(paramType);
                newParam.setName(paramName);
                newMethod.getParameters().add(newParam);
            }
        } catch (NullPointerException ex) {
        }

        parentClass.getMethods().add(newMethod);
        if (method.getBody() == null) {
            return;
        }
        //iterate all statements in the method recursively and check for declarations/usages
        if (method.getBody().getStmts() != null) {
            for (Statement stmt : method.getBody().getStmts()) {
                parseStatement(stmt, method);
            }
        }

    }

    /**
     * adds the constructor to the class as a MethodNode
     * parses all statements in the constructor
     * @param parentClass the class containing the constructor
     * @param constructor the ConstructorDeclaration node
     */
    private void parseContentOfConstructor(ClassNode parentClass, ConstructorDeclaration constructor) {
        MethodNode newMethod = new MethodNode();
        String methodName = constructor.getName();
        String returnType = null;
        Visibility visibility = util.getVisibilityFromModifier(constructor.getModifiers());
        newMethod.setVisibility(visibility);
        newMethod.setName(methodName);
        newMethod.setReturnType(returnType);
        newMethod.setConstructor(true);
        newMethod.setStartPositionInLine(constructor.getBeginColumn());//TODO replace with line number
        int startLine = constructor.getBeginLine();
        String stringUntilName = constructor.toString().substring(0, constructor.toString().indexOf(constructor.getName() + "("));
        int linesUntilName = stringUntilName.split("\n").length - 1;
        if (constructor.getJavaDoc() != null) {
            linesUntilName -= constructor.getJavaDoc().getEndLine() - constructor.getJavaDoc().getBeginLine() + 1;
        }
        startLine += linesUntilName;

        newMethod.setStartLine(startLine);
        //parse all parameters to VariableNodes
        try {
            for (Parameter param : constructor.getParameters()) {
                VariableNode newParam = new VariableNode();
                String paramName = param.getId().getName();
                String paramType = param.getType().toString();
                newParam.setType(paramType);
                newParam.setStartLine(param.getBeginLine());
                newParam.setName(paramName);
                newMethod.getParameters().add(newParam);
            }
        } catch (NullPointerException ex) {
        }
        parentClass.getMethods().add(newMethod);
        //iterate all statements in the method recursively and check for declarations/usages
        if (constructor.getBlock() != null) {
            parseStatement(constructor.getBlock(), constructor);
        }
    }

    /**
     * parses the statement and all childStatements depending on it's actual type
     * @param stmt the statement that is to be parsed
     * @param parent the parent node of the statement
     */
    private void parseStatement(Statement stmt, Node parent) {
        stmt.setData(parent);
        if (stmt instanceof IfStmt) {
            parseIfStmt((IfStmt) stmt, parent);
        } else if (stmt instanceof BlockStmt) {
            parseBlockStmt((BlockStmt) stmt, parent);
        } else if (stmt instanceof ExpressionStmt) {
            parseExpression(((ExpressionStmt) stmt).getExpression(), stmt);
        } else if (stmt instanceof DoStmt) {
            DoStmt doStmt = (DoStmt) stmt;
            parseExpression(doStmt.getCondition(), stmt);
            parseStatement(doStmt.getBody(), stmt);
        } else if (stmt instanceof ForStmt) {
            ForStmt forStmt = (ForStmt) stmt;
            parseStatement(forStmt.getBody(), stmt);
            if (forStmt.getInit() != null) {
                for (Expression expr : forStmt.getInit()) {
                    parseExpression(expr, stmt);
                }
            }
        } else if (stmt instanceof ForeachStmt) {
            ForeachStmt foreachStmt = (ForeachStmt) stmt;
            parseStatement(foreachStmt.getBody(), stmt);
            parseExpression(foreachStmt.getVariable(), stmt);
            parseExpression(foreachStmt.getIterable(), stmt);
        } else if (stmt instanceof SwitchStmt) {
            SwitchStmt switchStmt = (SwitchStmt) stmt;
            parseExpression(switchStmt.getSelector(), stmt);
            for (SwitchEntryStmt switchEntry : switchStmt.getEntries()) {
                if (switchEntry.getStmts() != null) {
                    for (Statement switchEntryStatement : switchEntry.getStmts()) {
                        parseStatement(switchEntryStatement, stmt);
                    }
                }
            }
        } else if (stmt instanceof TryStmt) {
            TryStmt tryStmt = (TryStmt) stmt;
            parseStatement(tryStmt.getTryBlock(), stmt);
            if (tryStmt.getFinallyBlock() != null) {
                parseStatement(tryStmt.getFinallyBlock(), stmt);
            }
            if (tryStmt.getCatchs() == null) {
                return;
            }
            for (CatchClause catchClause : tryStmt.getCatchs()) {
                MethodNode parentMethod = util.getMethodAtLine(catchClause.getBeginLine());
                Parameter param = catchClause.getExcept();
                VariableNode var = new VariableNode();
                var.setParentLineDeclaration(catchClause.getCatchBlock().getBeginLine());
                var.setAttribute(false);
                var.setStartLine(param.getBeginLine());
                var.setStartPositionInLine(param.getBeginColumn());
                var.setType(param.getType().toString());
                var.setName(param.getId().getName());
                try {
                    parentMethod.getLocalVariables().add(var);
                } catch (NullPointerException ex) {
                    getClass(); //FIXME
                }
                parseStatement(catchClause.getCatchBlock(), stmt);
            }
        } else if (stmt instanceof WhileStmt) {
            WhileStmt whileStmt = (WhileStmt) stmt;
            parseExpression(whileStmt.getCondition(), stmt);
            parseStatement(whileStmt.getBody(), stmt);
        } else if (stmt instanceof ReturnStmt) {
            ReturnStmt returnStmt = (ReturnStmt) stmt;
            parseExpression(returnStmt.getExpr(), stmt);
        } else if (stmt instanceof ExplicitConstructorInvocationStmt) {
            ExplicitConstructorInvocationStmt ecis = (ExplicitConstructorInvocationStmt) stmt;
            parseExpression(ecis.getExpr(), ecis);
        } else if (stmt instanceof ThrowStmt) {
            ThrowStmt ts = (ThrowStmt) stmt;
            parseExpression(ts.getExpr(), ts);
        }
    }

    /**
     * parses the BlockStmt and all it's childStatements
     * @param stmt the BlockStmt
     * @param parent the parent node of the block statement
     */
    private void parseBlockStmt(BlockStmt stmt, Node parent) {
        stmt.setData(parent);
        if (stmt.getStmts() == null) {
            return;
        }
        for (Statement childStmt : stmt.getStmts()) {
            parseStatement(childStmt, stmt);
        }
    }

    /**
     * parses the expression and all child expressions depending on it's actual type
     * @param expr the expression
     * @param parent the parent node of the expression
     */
    private void parseExpression(Expression expr, Node parent) {
        if (expr == null) {
            return;
        }
        expr.setData(parent);
        if (expr instanceof VariableDeclarationExpr) {
            VariableDeclarationExpr vars = (VariableDeclarationExpr) expr;
            String type = vars.getType().toString();
            Visibility visibility = util.getVisibilityFromModifier(vars.getModifiers());
            MethodNode parentMethod = util.getMethodAtLine(expr.getBeginLine());
            if (parentMethod != null) {
                //in case it is an attribute
                for (VariableDeclarator variableDeclaration : ((VariableDeclarationExpr) expr).getVars()) {
                    VariableNode var = new VariableNode();
                    Node grandParent = (Node) parent.getData();
                    var.setParentLineDeclaration(grandParent.getBeginLine());
                    var.setAttribute(false);
                    var.setStartLine(expr.getBeginLine());
                    var.setStartPositionInLine(expr.getBeginColumn());
                    var.setVisibility(visibility);
                    var.setType(type);
                    var.setName(variableDeclaration.getId().getName());
                    parentMethod.getLocalVariables().add(var);
                    parseExpression(variableDeclaration.getInit(), expr);
                }
            }
        } else if (expr instanceof ConditionalExpr) {
            parseConditionExpr((ConditionalExpr) expr, parent);
        } else if (expr instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) expr;
            if (methodCallExpr.getArgs() != null) {
                for (Expression currentExpr : methodCallExpr.getArgs()) {
                    parseExpression(currentExpr, expr);
                }
            }
            parseExpression(methodCallExpr.getScope(), methodCallExpr);
        } else if (expr instanceof ArrayAccessExpr) {
            ArrayAccessExpr aae = (ArrayAccessExpr) expr;
            parseExpression(aae.getIndex(), expr);
            parseExpression(aae.getName(), expr);
        } else if (expr instanceof ArrayCreationExpr) {
            ArrayCreationExpr ace = (ArrayCreationExpr) expr;
            if (ace.getDimensions() != null) {
                for (Expression currentExpr : ace.getDimensions()) {
                    parseExpression(currentExpr, expr);
                }
            }
            parseExpression(ace.getInitializer(), expr);
        } else if (expr instanceof ArrayInitializerExpr) {
            ArrayInitializerExpr aie = (ArrayInitializerExpr) expr;
            if (aie.getValues() != null) {
                for (Expression currentExpr : aie.getValues()) {
                    parseExpression(currentExpr, expr);
                }
            }
        } else if (expr instanceof AssignExpr) {
            AssignExpr ae = (AssignExpr) expr;
            parseExpression(ae.getValue(), expr);
            parseExpression(ae.getTarget(), expr);
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr) expr;
            parseExpression(be.getLeft(), expr);
            parseExpression(be.getRight(), expr);
        } else if (expr instanceof CastExpr) {
            CastExpr ce = (CastExpr) expr;
            parseExpression(ce.getExpr(), expr);
        } else if (expr instanceof ConditionalExpr) {
            ConditionalExpr ce = (ConditionalExpr) expr;
            parseExpression(ce.getCondition(), expr);
            parseExpression(ce.getElseExpr(), expr);
            parseExpression(ce.getThenExpr(), expr);
        } else if (expr instanceof EnclosedExpr) {
            EnclosedExpr ee = (EnclosedExpr) expr;
            parseExpression(ee.getInner(), expr);
        } else if (expr instanceof FieldAccessExpr) {
            FieldAccessExpr fae = (FieldAccessExpr) expr;
            parseExpression(fae.getScope(), expr);
        } else if (expr instanceof InstanceOfExpr) {
            InstanceOfExpr ioe = (InstanceOfExpr) expr;
            parseExpression(ioe.getExpr(), parent);
        } else if (expr instanceof ObjectCreationExpr) {
            ObjectCreationExpr oje = (ObjectCreationExpr) expr;
            if (oje.getArgs() != null) {
                for (Expression arg : oje.getArgs()) {
                    parseExpression(arg, expr);
                }
            }
            parseExpression(oje.getScope(), expr);
        } else if (expr instanceof SingleMemberAnnotationExpr) {
            SingleMemberAnnotationExpr smae = (SingleMemberAnnotationExpr) expr;
            parseExpression(smae.getMemberValue(), expr);
        } else if (expr instanceof SuperExpr) {
            SuperExpr se = (SuperExpr) expr;
            parseExpression(se.getClassExpr(), expr);
        } else if (expr instanceof ThisExpr) {
            ThisExpr te = (ThisExpr) expr;
            parseExpression(te.getClassExpr(), expr);
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr ue = (UnaryExpr) expr;
            parseExpression(ue.getExpr(), expr);
        } else if (expr instanceof ObjectCreationExpr) {
            ObjectCreationExpr oce = (ObjectCreationExpr) expr;
            for (Expression ex : oce.getArgs()) {
                parseExpression(ex, expr);
            }
        }
    }

    /**
     * parses the condition of the expression
     * @param expr the condition expression
     * @param parent the parent node of the expression
     */
    private void parseConditionExpr(Expression expr, Node parent) {
        expr.setData(parent);
        if (expr instanceof ConditionalExpr) {
            //TODO implement
            return;
        }
        if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr) expr;
            parseConditionExpr(unaryExpr.getExpr(), parent);
            return;
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) expr;
            parseConditionExpr(binaryExpr.getLeft(), binaryExpr);
            parseConditionExpr(binaryExpr.getRight(), binaryExpr);
        } else if (expr instanceof EnclosedExpr) {
            EnclosedExpr enclosedExpr = (EnclosedExpr) expr;
            parseConditionExpr(enclosedExpr.getInner(), parent);
        } else if (expr instanceof MethodCallExpr) {
            parseExpression(expr, parent);
        } else if (expr != null) {
            return;
        }
    }

    /**
     * parses the IfStmt, the condition, the thenStatement and the elseStatement
     * @param stmt the IfStmt
     * @param parent the parent node of the if statement
     */
    private void parseIfStmt(IfStmt stmt, Node parent) {
        stmt.setData(parent);
        parseConditionExpr(stmt.getCondition(), stmt);
        //TODO add usage handling for condition expressions
        parseStatement(stmt.getThenStmt(), stmt);
        if (stmt.getElseStmt() != null) {
            parseStatement(stmt.getElseStmt(), stmt);
        }
    }

    @Deprecated
    private void parseAbsoluteCharPositions() {
        int lineNumber = 0;
        int absoluteChars = 0;
        String[] lines = fileContent.split("\n");
        for (AstNode currentNode : fileNode.getChildNodes()) {
            boolean elementFoundOnLine = false;
            while (!elementFoundOnLine && lineNumber < lines.length) {
                if (currentNode.getStartLine() == lineNumber) {
                    //TODO add recursive method that parses the line numbers for variables defined in methods
                    currentNode.setStartPositionAbsolute(absoluteChars + currentNode.getStartPositionInLine());
                    elementFoundOnLine = true;
                } else {
                    lineNumber++;
                }

            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        return "text/x-java-source";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "0.1-SNAPSHOT";
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getTypeDeclarations() throws CodeAnalyzerPluginException {
        if (typeDeclarations == null) {
            throw new CodeAnalyzerPluginException("No type declaration information available, you must first analyze a file");
        }
        return typeDeclarations;
    }

    /** {@inheritDoc} */
    @Override
    public List<Usage> getUsages() throws CodeAnalyzerPluginException {
        if (usages == null) {
            throw new CodeAnalyzerPluginException("No usage information available, you must first analyze a file");
        }
        return usages;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getImports() {
        return imports;
    }

    /** {@inheritDoc} */
    @Override
    public AstNode getAst() {
        return fileNode;
    }
}
