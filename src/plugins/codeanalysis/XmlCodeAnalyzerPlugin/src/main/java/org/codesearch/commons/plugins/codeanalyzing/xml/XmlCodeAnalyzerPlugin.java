package org.codesearch.commons.plugins.codeanalyzing.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.codeanalyzing.xml.ast.XmlNode;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Analyzes XML files.
 * @author Samuel Kogler
 */
@Component
public class XmlCodeAnalyzerPlugin implements CodeAnalyzerPlugin {

    private XmlNode ast = new XmlNode();
    private Stack<XmlNode> nodes = new Stack<XmlNode>();

    @Override
    public List<String> getTypeDeclarations() throws CodeAnalyzerPluginException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Usage> getUsages() throws CodeAnalyzerPluginException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void analyzeFile(String fileContent) throws CodeAnalyzerPluginException {
        ByteArrayInputStream bais = new ByteArrayInputStream(fileContent.getBytes());
        try {
            nodes.clear();
            ast = new XmlNode();
            nodes.add(ast);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new LocationDefaultHandler());
            xmlReader.parse(new InputSource(bais));
        } catch (IOException ex) {
            //TODO add exception handling
            Logger.getLogger(XmlCodeAnalyzerPlugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XmlCodeAnalyzerPlugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XmlCodeAnalyzerPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    class LocationDefaultHandler extends DefaultHandler {
        Locator locator;

        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }
        // This method is called when an element is encountered

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
            XmlNode newNode = new XmlNode();
            newNode.setName(qName);
            newNode.setStartLine(locator.getLineNumber());
            nodes.peek().getXmlNodes().add(newNode);
            nodes.add(newNode);
        }

        @Override
        public void endElement(String string, String string1, String string2) throws SAXException {
            nodes.pop();
        }


    }

    @Override
    public AstNode getAst() {
        return ast;
    }

    @Override
    public List<String> getImports() throws CodeAnalyzerPluginException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getPurposes() {
        return "xml"; //FIXME
    }

    @Override
    public String getVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
