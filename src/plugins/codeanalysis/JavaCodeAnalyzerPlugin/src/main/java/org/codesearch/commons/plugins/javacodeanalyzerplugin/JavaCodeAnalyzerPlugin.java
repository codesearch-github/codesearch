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
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.WhileStmt;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.database.DBAccess;
import org.codesearch.commons.database.DatabaseAccessException;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalLink;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalMethodLink;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalUsage;
import org.codesearch.commons.plugins.codeanalyzing.ast.ExternalVariableLink;

import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.codeanalyzing.ast.Visibility;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ClassNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.FileNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.MethodNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.VariableNode;
import org.springframework.stereotype.Component;

/**
 *
 * @author David Froehlich
 */
@Component
public class JavaCodeAnalyzerPlugin implements CodeAnalyzerPlugin {

    private FileNode fileNode = new FileNode();
    private List<AstNode> ast = new LinkedList<AstNode>();
    private List<Usage> usages;
    private String fileContent;
    private List<String> typeDeclarations;
    private List<ExternalLink> externalLinks;
    private List<String> imports;
    AnalyzerUtil util;

    @Override
    public List<AstNode> getAstForCurrentFile() throws CodeAnalyzerPluginException {
        return ast;
    }

    //TODO remove repository
    @Override
    public void analyzeFile(final String fileContent, RepositoryDto repository) throws CodeAnalyzerPluginException {
        CompilationUnit cu = null;
        ast = new LinkedList<AstNode>();
        fileNode = new FileNode();
        util = new AnalyzerUtil(fileNode);
        this.fileContent = fileContent;
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(fileContent.getBytes()); //TODO maybe change specification of analyzeFile to take an inputStream as parameter
            cu = JavaParser.parse(bais);
            buildAST(cu);
            fileNode.addCompoundNodesToList(ast);
            //parse usages via UsageVisitor
            String packageName = null;
            try{
                packageName = cu.getPackage().getName().toString();
            }catch(NullPointerException ex){
                //in case the class has no package
            }
            
            UsageVisitor uv = new UsageVisitor(util, packageName);
            cu.accept(uv, null);
            usages = util.getUsages();
            typeDeclarations = uv.getTypeDeclarations();
            externalLinks = util.getExternalLinks();
            imports = uv.getImports();
            Collections.sort(usages);
            Collections.sort(ast);
            parseAbsoluteCharPositions();
            
        } catch (ParseException ex) {
            System.out.println(ex.toString());
            //TODO throw exception
        } finally {
            try {
                bais.close();
            } catch (IOException ex) {
            }
        }
    }

    private void buildAST(CompilationUnit cu) {
        //iterate all types (classes) from the compound node
        for (TypeDeclaration type : cu.getTypes()) {
            parseContentOfClass(type);
        }
    }

    private void parseContentOfClass(TypeDeclaration type) {
        //create ClassNode and extract required info from TypeDeclaration
        ClassNode newClazz = new ClassNode();
        int startLine = type.getBeginLine();
        //    int endLine = n.getEndLine();
        String clazzName = type.getName();
        int nodeLength = type.getEndColumn() - type.getBeginColumn();
        newClazz.setName(clazzName);
        newClazz.setStartLine(startLine);
        newClazz.setStartPositionInLine(type.getBeginColumn());
        newClazz.setNodeLength(nodeLength);
        fileNode.getClasses().add(newClazz);
        //iterate all methods and attributes in class
        if (type.getMembers() != null) {
            for (BodyDeclaration member : type.getMembers()) {
                if (member instanceof ClassOrInterfaceDeclaration) {
                    parseContentOfClass((TypeDeclaration) member);
                } else if (member instanceof MethodDeclaration) {
                    parseContentOfMethod(newClazz, (MethodDeclaration) member);
                } else if (member instanceof ConstructorDeclaration) {
                    parseContentOfConstructor(newClazz, (ConstructorDeclaration) member);
                } else if (member instanceof FieldDeclaration) {
                    FieldDeclaration fieldDeclaration = (FieldDeclaration) member;
                    for (VariableDeclarator v : fieldDeclaration.getVariables()) {
                        VariableNode newVariable = new VariableNode();
                        newVariable.setName(v.getId().getName());
                        newVariable.setType(fieldDeclaration.getType().toString());
                        newVariable.setStartLine(fieldDeclaration.getBeginLine());
                        newVariable.setStartPositionInLine(fieldDeclaration.getBeginColumn());
                        newVariable.setAttribute(true);
                        newClazz.getAttributes().add(newVariable);
                    }
                }
            }
        }
    }

    private void parseContentOfMethod(ClassNode parentClass, MethodDeclaration method) {
        MethodNode newMethod = new MethodNode();
        String methodName = method.getName();
        String returnType = method.getType().toString();
        Visibility visibility = util.getVisibilityFromModifier(method.getModifiers());
        newMethod.setVisibility(visibility);
        newMethod.setName(methodName);
        newMethod.setReturnType(returnType);
        newMethod.setConstructor(false);
        newMethod.setStartPositionInLine(method.getBeginColumn());//TODO replace with line number
        newMethod.setStartLine(method.getBeginLine());

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

    private void parseStatement(Statement stmt, Node parent) {
        stmt.setData(parent);
        try {
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
                for (Expression expr : forStmt.getInit()) {
                    parseExpression(expr, stmt);
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
                    for (Statement switchEntryStatement : switchEntry.getStmts()) {
                        parseStatement(switchEntryStatement, stmt);
                    }
                }
            } else if (stmt instanceof TryStmt) {
                TryStmt tryStmt = (TryStmt) stmt;
                parseStatement(tryStmt.getTryBlock(), stmt);
                parseStatement(tryStmt.getFinallyBlock(), stmt);
                if (tryStmt.getCatchs() == null) {
                    return;
                }
                for (CatchClause catchClause : tryStmt.getCatchs()) {
                    MethodNode parentMethod = util.getMethodAtLine(catchClause.getBeginLine());
                    Parameter param = catchClause.getExcept();
                    VariableNode var = new VariableNode();
                    var.setParentLineDeclaration(parent.getBeginLine());
                    var.setAttribute(false);
                    var.setStartLine(param.getBeginLine());
                    var.setStartPositionInLine(param.getBeginColumn());
                    var.setType(param.getType().toString());
                    var.setName(param.getId().getName());
                    parentMethod.getLocalVariables().add(var);
                    parseStatement(catchClause.getCatchBlock(), stmt);
                }
            } else if (stmt instanceof WhileStmt) {
                WhileStmt whileStmt = (WhileStmt) stmt;
                parseExpression(whileStmt.getCondition(), stmt);
                parseStatement(whileStmt.getBody(), stmt);
            } else if (stmt instanceof ReturnStmt) {
                ReturnStmt returnStmt = (ReturnStmt) stmt;
                parseExpression(returnStmt.getExpr(), stmt);
            } else {
            }
        } catch (NullPointerException ex) {
        }
        //TODO implement handling for conditions
    }

    private void parseBlockStmt(BlockStmt stmt, Node parent) {
        stmt.setData(parent);
        if (stmt.getStmts() == null) {
            return;
        }
        for (Statement childStmt : stmt.getStmts()) {
            parseStatement(childStmt, stmt);
        }
    }

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
                    Node grandParent = (Node)parent.getData();
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
            for (Expression currentExpr : methodCallExpr.getArgs()) {
                parseExpression(currentExpr, expr);
            }
        } else if (expr instanceof ArrayAccessExpr) {
            ArrayAccessExpr aae = (ArrayAccessExpr) expr;
            parseExpression(aae.getIndex(), expr);
            parseExpression(aae.getName(), expr);
        } else if (expr instanceof ArrayCreationExpr) {
            ArrayCreationExpr ace = (ArrayCreationExpr) expr;
            for (Expression currentExpr : ace.getDimensions()) {
                parseExpression(currentExpr, expr);
            }
            parseExpression(ace.getInitializer(), expr);
        } else if (expr instanceof ArrayInitializerExpr) {
            ArrayInitializerExpr aie = (ArrayInitializerExpr) expr;
            for (Expression currentExpr : aie.getValues()) {
                parseExpression(currentExpr, expr);
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
            for (Expression arg : oje.getArgs()) {
                parseExpression(arg, expr);
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
        }

        //TODO check for ArrayCreationExpr and ArrayInitializerExpr
    }

    private void parseConditionExpr(Expression expr, Node parent) {
        expr.setData(parent);
        if (expr instanceof ConditionalExpr) {
            //TODO implement
            return;
        }
        if (expr instanceof NameExpr) {
            String varName = expr.toString();
            VariableNode refVar = util.getVariableDeclarationForUsage(expr.getBeginLine(), varName, parent);
            if (refVar != null) {
                int referenceLineNumber = refVar.getStartLine();
                usages.add(new Usage(varName.length(), expr.getBeginColumn(), expr.getBeginLine(), referenceLineNumber, varName));
            }
        } else if (expr instanceof UnaryExpr) {
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
        } else if (expr != null) {
            return;
        }
    }

    private void parseIfStmt(IfStmt stmt, Node parent) {
        if (stmt.getBeginLine() == 96) {
            stmt.getBeginColumn();
        }
        stmt.setData(parent);
        parseConditionExpr(stmt.getCondition(), stmt);
        //TODO add usage handling for condition expressions
        parseStatement(stmt.getThenStmt(), stmt);
        parseStatement(stmt.getElseStmt(), stmt);
    }

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
        newMethod.setStartLine(constructor.getBeginLine());
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
    }

    private void parseAbsoluteCharPositions() {
        int lineNumber = 0;
        int absoluteChars = 0;
        String[] lines = fileContent.split("\n");
        for (AstNode currentNode : ast) {
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

    @Override
    public String getPurposes() {
        return "java"; //FIXME
    }

    @Override
    public String getVersion() {
        return "0.1-SNAPSHOT";
    }

    @Override
    public List<String> getTypeDeclarations() throws CodeAnalyzerPluginException {
        return typeDeclarations;
    }

    @Override
    public List<Usage> getUsages() throws CodeAnalyzerPluginException {
        return usages;
    }

    @Override
    public List<ExternalLink> getExternalLinks() {
        return externalLinks;
    }

    @Override
    public List<ExternalUsage> parseExternalLinks(String fileContent, List<String> imports, List<ExternalLink> externalLinks, String repository) {
        List<ExternalUsage> usages = new LinkedList<ExternalUsage>(); //TODO rename
        try {
            for (ExternalLink el : externalLinks) {
                String fullyQualifiedName = null;
                String filePath = "";
                int referenceLineNumber = 0;
                int startLine = el.getLineNumber();
                int column = el.getColumn();
                int length = el.getLength();
                String replacedString = el.getClassName();
                for (String currentImport : imports) {
                    if (currentImport.endsWith(el.getClassName())) {
                        fullyQualifiedName = currentImport;
                    }
                }
                if (fullyQualifiedName != null) {
                    filePath = DBAccess.getFilePathForTypeDeclaration(fullyQualifiedName, repository);
                    if (filePath == null) {
                        continue;
                    }
                } else {
//                    throw new NotImplementedException();
                    //In case the file is importet through an asterisk import
                }
                if (el instanceof ExternalMethodLink) {
                    List<AstNode> ast = DBAccess.getBinaryIndexForFile(filePath, repository); //TODO rename
                } else if (el instanceof ExternalVariableLink) {
                    ExternalVariableLink extVarLink = (ExternalVariableLink) el;
                    List<AstNode> fileAst = (List<AstNode>) DBAccess.getBinaryIndexForFile(filePath, repository);
                    for (AstNode currentNode : fileAst) {
                        if (currentNode instanceof VariableNode && currentNode.getName().equals(extVarLink.getVariableName())) {
                            referenceLineNumber = currentNode.getStartLine();
                        }
                    }
                }
                usages.add(new ExternalUsage(column, startLine, length, referenceLineNumber, replacedString, filePath));
            }
        } catch (DatabaseAccessException ex) {
            System.out.println(ex);
            //FIXME
        }

        return usages;
    }

    @Override
    public List<String> getImports() {
        return imports;
    }
}
