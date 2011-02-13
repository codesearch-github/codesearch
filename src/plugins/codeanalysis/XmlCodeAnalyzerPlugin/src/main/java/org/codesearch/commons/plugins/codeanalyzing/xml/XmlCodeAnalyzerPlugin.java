package org.codesearch.commons.plugins.codeanalyzing.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPlugin;
import org.codesearch.commons.plugins.codeanalyzing.CodeAnalyzerPluginException;
import org.codesearch.commons.plugins.codeanalyzing.ast.AstNode;
import org.codesearch.commons.plugins.codeanalyzing.ast.Usage;
import org.codesearch.commons.plugins.codeanalyzing.xml.ast.XmlNode;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Analyzes XML files.
 * @author Samuel Kogler
 */
public class XmlCodeAnalyzerPlugin implements CodeAnalyzerPlugin {

    private XmlNode ast = new XmlNode();
    private Stack<XmlNode> nodes = new Stack<XmlNode>();

    /** {@inheritDoc} */
    @Override
    public List<String> getTypeDeclarations() throws CodeAnalyzerPluginException {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public List<Usage> getUsages() throws CodeAnalyzerPluginException {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
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
            xmlReader.setEntityResolver(new EntityResolver() {

                public InputSource resolveEntity(String publicId, String systemId) {
                    return new InputSource(new ByteArrayInputStream("<?xml version='1.0'encoding='UTF-8'?>".getBytes()));
                }
            });
            xmlReader.setContentHandler(new LocationDefaultHandler());
            xmlReader.parse(new InputSource(bais));
        } catch (UnknownHostException ex) {
        } catch (IOException ex) {
        } catch (ParserConfigurationException ex) {
            throw new CodeAnalyzerPluginException("Exception while trying to analyze the file\n" + ex);
        } catch (SAXException ex) {
            throw new CodeAnalyzerPluginException("Exception while trying to analyze the file\n" + ex);
        }

    }

    /**
     * DefaultHandler that parses elements in an XML file to XmlNodes and adds the line number of creation via a Locator
     */
    class LocationDefaultHandler extends DefaultHandler {

        Locator locator;

        /** {@inheritDoc} */
        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        /**
         * creates a new XmlNode with the name and line number of the element from the xml files
         * @param namespaceURI dummy parameter
         * @param localName dummy parameter
         * @param qName name of the node
         * @param atts dummy parameter
         */
        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
            XmlNode newNode = new XmlNode();
            newNode.setName(qName);
            newNode.setStartLine(locator.getLineNumber());
            nodes.peek().getXmlNodes().add(newNode);
            nodes.add(newNode);
        }

        /**
         * pops the node from the node stack so the XmlNode tree can be parsed correctly
         * @param string dummy parameter
         * @param string1 dummy parameter
         * @param string2 dummy parameter
         * @throws SAXException
         */
        @Override
        public void endElement(String string, String string1, String string2) throws SAXException {
            nodes.pop();
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
            return new InputSource(
                    new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public AstNode getAst() {
        return ast;
    }

    /**
     * returns an empty list, since an XML file can't have any imports
     * @return Collections.Empty_List
     * @throws CodeAnalyzerPluginException can't really happen
     */
    @Override
    public List<String> getImports() throws CodeAnalyzerPluginException {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public String getPurposes() {
        return "application/xml";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "1.0";
    }
}
