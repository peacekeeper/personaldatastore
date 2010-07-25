package pds.web.tools.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtils {

	public static final String TOKENFORMAT_SAML_11 = "SAML 1.1";
	public static final String TOKENFORMAT_SAML_20 = "SAML 2.0";

	private static final String ATTRIBUTE_NAMESPACE_SAML_20 = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";
	private static final String ATTRIBUTE_NAMEFORMAT_SAML_20 = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";

	private static DocumentBuilder documentBuilder;

	static {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);

		try {

			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (Exception ex) {

			throw new RuntimeException("Failed to create DocumentBuilder", ex);
		}
	}

	public static Document newDocument() {

		return documentBuilder.newDocument();
	}

	public static Document parseXmlBytesToDocument(byte[] xmlBytes) {

		if (xmlBytes == null) return null;

		Document document;

		try {

			InputSource source = new InputSource(new ByteArrayInputStream(xmlBytes));

			document = documentBuilder.parse(source);
		} catch (IOException ex) {

			throw new RuntimeException("Failed to parse input stream due to I/O errors: " + ex.getMessage(), ex);
		} catch (SAXException ex) {

			throw new RuntimeException("Failed to parse input stream due to SAX errors: " + ex.getMessage(), ex);
		}

		return document;
	}

	public static Document parseXmlStringToDocument(String xmlString) {

		if (xmlString == null) return null;

		Document document;

		try {

			InputSource source = new InputSource(new StringReader(xmlString));

			document = documentBuilder.parse(source);
		} catch (IOException ex) {

			throw new RuntimeException("Failed to parse input stream due to I/O errors: " + ex.getMessage(), ex);
		} catch (SAXException ex) {

			throw new RuntimeException("Failed to parse input stream due to SAX errors: " + ex.getMessage(), ex);
		}

		return document;
	}

	public static String formatXmlAsString(Document document) {

		StringWriter writer = new StringWriter();
		TransformerFactory factory = TransformerFactory.newInstance();

		try {

			factory.setAttribute("indent-number", new Integer(2));
		} catch (IllegalArgumentException ex) { 

		}

		try {

			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			transformer.transform(new DOMSource(document), new StreamResult(writer));
		} catch (TransformerException ex) {

			throw new RuntimeException("Error formatting xml as pretty-printed string", ex);
		}

		return writer.toString();
	}

	public static String formatXmlAsStringNoWhitespace(Document document) {

		StringWriter writer = new StringWriter();
		TransformerFactory factory = TransformerFactory.newInstance();

		try {

			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

			transformer.transform(new DOMSource(document), new StreamResult(writer));
		} catch (TransformerException ex) {

			throw new RuntimeException("Error formatting xml as no-whitespace string", ex);
		}

		return writer.toString();
	}

	public static String guessTokenFormat(Document document) {

		String minorVersion = document.getDocumentElement().getAttribute("MinorVersion");
		String majorVersion = document.getDocumentElement().getAttribute("MajorVersion");
		String version = document.getDocumentElement().getAttribute("Version");

		if ("1".equals(majorVersion) && "1".equals(minorVersion)) {

			return "SAML 1.1";
		} else if ("2.0".equals(version)) {

			return "SAML 2.0";
		} else {

			return null;
		}
	}

	public static Map<String, String> createMapFromSaml11Token(Document document) {

		Map<String, String> map = new HashMap<String, String>();
		Element element = document.getDocumentElement();
		String ns = element.getNamespaceURI();
		NodeList attrNodes = document.getElementsByTagNameNS(ns, "Attribute");

		for (int i = 0; i < attrNodes.getLength(); i++) {

			Element attrElement = ((Element) attrNodes.item(i));
			String name = attrElement.getAttribute("AttributeName");
			String namespace = attrElement.getAttribute("AttributeNamespace");
			String key;

			if (ATTRIBUTE_NAMESPACE_SAML_20.equals(namespace)) {

				key = name;
			} else {

				key = namespace + (name.startsWith("/") ? name : "/" + name);
			}

			Node attrValue = attrElement.getElementsByTagNameNS(ns, "AttributeValue").item(0).getFirstChild();
			String value;

			if (attrValue != null) {

				value = attrValue.getNodeValue();
			} else {

				value = "";
			}

			map.put(key, value);
		}

		return map;
	}

	public static Map<String, String> createMapFromSaml20Token(Document document) {

		Map<String, String> map = new HashMap<String, String>();
		Element element = document.getDocumentElement();
		String ns = element.getNamespaceURI();
		NodeList attrNodes = document.getElementsByTagNameNS(ns, "Attribute");

		for (int i = 0; i < attrNodes.getLength(); i++) {

			Element attrElement = ((Element) attrNodes.item(i));
			String name = attrElement.getAttribute("Name");
			String nameformat = attrElement.getAttribute("NameFormat");
			String key;

			if (ATTRIBUTE_NAMEFORMAT_SAML_20.equals(nameformat)) {

				key = name;
			} else {

				continue;
			}

			Node attrValue = attrElement.getElementsByTagNameNS(ns, "AttributeValue").item(0).getFirstChild();
			String value;

			if (attrValue != null) {

				value = attrValue.getNodeValue();
			} else {

				value = "";
			}

			map.put(key, value);
		}

		return map;
	}

	public static Node getElementByTagName(Document document, String tagname) {

		NodeList nodeList = document.getElementsByTagName(tagname);

		if (nodeList != null && nodeList.getLength() > 0) {

			return nodeList.item(0);
		} else {

			return null;
		}
	}

	public static Node getElementByTagName(Element element, String tagname) {

		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList != null && nodeList.getLength() > 0) {

			return nodeList.item(0);
		} else {

			return null;
		}
	}

	public static String xmlEncode(String string) {

		return string
		.replace("&", "&amp;")
		.replace("'", "&#39;")
		.replace("\"", "&quot;")
		.replace("<", "&lt;")
		.replace(">", "&gt;");
	}

	public static String xmlDecode(String string) {

		return string
		.replace("&amp;", "&")
		.replace("&#39;", "'")
		.replace("&quot;", "\"")
		.replace("&lt;", "<")
		.replace("&gt;", ">");
	}

	public static String getTextContent(Node node) {

		if (node == null) throw new NullPointerException();

		String textContent;

		textContent = node.getTextContent();
		if (textContent != null) return xmlDecode(textContent);

		NodeList childNodes = node.getChildNodes();
		if (childNodes.getLength() > 0 && childNodes.item(0) instanceof Text) textContent = node.getNodeValue();
		if (textContent != null) return xmlDecode(textContent);

		return "";
	}

	public static void setTextContent(Node node, String nodeValue) {

		if (node == null || nodeValue == null) throw new NullPointerException();

		node.appendChild(node.getOwnerDocument().createTextNode(xmlEncode(nodeValue)));
	}

	public static void appendElementText(Element parentElement, String elementName, String elementValue) {

		if (parentElement == null || elementName == null || elementValue == null) throw new NullPointerException();

		Element childElement = parentElement.getOwnerDocument().createElement(elementName);
		setTextContent(childElement, elementValue);
		parentElement.appendChild(childElement);
	}

	public static String getElementText(Element parentElement, String elementName) {

		if (parentElement == null || elementName == null) throw new NullPointerException();

		Element childElement = (Element) parentElement.getElementsByTagName(elementName).item(0);
		return getTextContent(childElement);
	}
}
