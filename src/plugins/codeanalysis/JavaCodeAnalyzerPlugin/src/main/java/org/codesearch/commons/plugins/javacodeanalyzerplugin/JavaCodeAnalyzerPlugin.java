/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codesearch.commons.plugins.javacodeanalyzerplugin;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codesearch.commons.configuration.xml.dto.RepositoryDto;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.CompoundNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Node;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.ClassNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.FileNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.MethodNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.VariableNode;
import org.codesearch.commons.plugins.javacodeanalyzerplugin.ast.Visibility;

/**
 *
 * @author David Froehlich
 */
public class JavaCodeAnalyzerPlugin implements CodeAnalyzerPlugin {

    private FileNode fileNode = new FileNode();
    private List<CompoundNode> ast = new LinkedList<CompoundNode>();
    private Map<Integer, Usage> usages = new HashMap<Integer, Usage>();
    private String fileContent;

    @Override
    public List<CompoundNode> getAstForCurrentFile() throws CodeAnalyzerPluginException {
        return ast;
    }

    @Override
    public void analyzeFile(final String fileContent, RepositoryDto repository) throws CodeAnalyzerPluginException {
        CompilationUnit cu = null;
        this.fileContent = fileContent;
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(fileContent.getBytes()); //TODO maybe change specification of analyzeFile to take an inputStream as parameter
            cu = JavaParser.parse(bais);
            buildAST(cu);

            fileNode.addCompoundNodesToList(ast);

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
        for (CompoundNode node : ast) {
            System.out.println(node.getOutlineLink());
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
        //iterate all methods in class
        for (BodyDeclaration member : type.getMembers()) {
            if (member instanceof ClassOrInterfaceDeclaration) {
                parseContentOfClass((TypeDeclaration) member);
            } else if (member instanceof MethodDeclaration) {
                parseContentOfMethod(newClazz, (MethodDeclaration) member);
            } else if (member instanceof ConstructorDeclaration) {
                parseContentOfConstructor(newClazz, (ConstructorDeclaration)member);
            }
        }
        fileNode.getClasses().add(newClazz);
    }

    private void parseContentOfMethod(ClassNode parentClass, MethodDeclaration method) {
        MethodNode newMethod = new MethodNode();
        String methodName = method.getName();
        String returnType = method.getType().toString();
        Visibility visibility;
        switch (method.getModifiers()) {
            case 0:
                visibility = Visibility.default_vis;
                break;
            case 1:
                visibility = Visibility.public_vis;
                break;
            case 2:
                visibility = Visibility.private_vis;
                break;
            default:
                visibility = Visibility.default_vis;
            //TODO implement other cases
        }
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
                String paramName = param.getId().getName();
                String paramType = param.getType().toString();
                newParam.setType(paramType);
                newParam.setName(paramName);
                newMethod.getParameters().add(newParam);
            }
        } catch (NullPointerException ex) {
        }
        parentClass.getMethods().add(newMethod);
    }

    private void parseContentOfConstructor(ClassNode parentClass, ConstructorDeclaration method){
        MethodNode newMethod = new MethodNode();
        String methodName = method.getName();
        String returnType = null;
        Visibility visibility;
        switch (method.getModifiers()) {
            case 0:
                visibility = Visibility.default_vis;
                break;
            case 1:
                visibility = Visibility.public_vis;
                break;
            case 2:
                visibility = Visibility.private_vis;
                break;
            default:
                visibility = Visibility.default_vis;
            //TODO implement other cases
        }
        newMethod.setVisibility(visibility);
        newMethod.setName(methodName);
        newMethod.setReturnType(returnType);
        newMethod.setConstructor(true);
        newMethod.setStartPositionInLine(method.getBeginColumn());//TODO replace with line number
        newMethod.setStartLine(method.getBeginLine());
        //parse all parameters to VariableNodes
        try {
            for (Parameter param : method.getParameters()) {
                VariableNode newParam = new VariableNode();
                String paramName = param.getId().getName();
                String paramType = param.getType().toString();
                newParam.setType(paramType);
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
        for (Node currentNode : ast) {
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

    public String getPurposes() {
        return "java"; //FIXME
    }

    public String getVersion() {
        return "0.1-SNAPSHOT";
    }
}
