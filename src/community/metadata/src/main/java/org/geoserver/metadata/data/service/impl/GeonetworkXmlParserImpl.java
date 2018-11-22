/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.geoserver.metadata.data.dto.AttributeComplexTypeMapping;
import org.geoserver.metadata.data.dto.AttributeMapping;
import org.geoserver.metadata.data.dto.AttributeMappingConfiguration;
import org.geoserver.metadata.data.dto.FieldTypeEnum;
import org.geoserver.metadata.data.dto.OccurenceEnum;
import org.geoserver.metadata.data.dto.impl.AttributeMappingImpl;
import org.geoserver.metadata.data.model.ComplexMetadataAttribute;
import org.geoserver.metadata.data.model.ComplexMetadataMap;
import org.geoserver.metadata.data.service.GeonetworkXmlParser;
import org.geoserver.metadata.data.service.YamlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Repository
public class GeonetworkXmlParserImpl implements GeonetworkXmlParser {

    private static final long serialVersionUID = -4931070325217885824L;

    @Autowired private YamlService yamlService;

    @Override
    public void parseMetadata(Document doc, ComplexMetadataMap metadataMap) throws IOException {
        AttributeMappingConfiguration mapping = yamlService.readMapping();

        for (AttributeMapping attributeMapping : mapping.getGeonetworkmapping()) {
            addAttribute(metadataMap, attributeMapping, doc, null, mapping.getObjectmapping());
        }
    }

    private void addAttribute(
            ComplexMetadataMap metadataMap,
            AttributeMapping attributeMapping,
            Document doc,
            Node node,
            List<AttributeComplexTypeMapping> mapping) {
        NodeList nodes = findNode(doc, attributeMapping.getGeonetwork(), node);

        if (nodes != null && nodes.getLength() > 0) {
            switch (attributeMapping.getOccurrence()) {
                case SINGLE:
                    mapNode(metadataMap, attributeMapping, doc, nodes.item(0), mapping);
                    break;
                case REPEAT:
                    for (int count = 0; count < nodes.getLength(); count++) {
                        mapNode(metadataMap, attributeMapping, doc, nodes.item(count), mapping);
                    }
                    break;
            }
        }
    }

    private void mapNode(
            ComplexMetadataMap metadataMap,
            AttributeMapping attributeMapping,
            Document doc,
            Node node,
            List<AttributeComplexTypeMapping> mapping) {
        if (FieldTypeEnum.COMPLEX.equals(attributeMapping.getFieldType())) {
            for (AttributeComplexTypeMapping complexTypeMapping : mapping) {
                if (attributeMapping.getTypename().equals(complexTypeMapping.getTypename())) {
                    ComplexMetadataMap submap = metadataMap.subMap(attributeMapping.getGeoserver());
                    for (AttributeMapping aMapping : complexTypeMapping.getMapping()) {
                        AttributeMapping am = new AttributeMappingImpl(aMapping);
                        am.setOccurrence(attributeMapping.getOccurrence());
                        addAttribute(submap, am, doc, node, mapping);
                    }
                    break;
                }
            }
        } else {
            if (OccurenceEnum.SINGLE.equals(attributeMapping.getOccurrence())) {
                ComplexMetadataAttribute<String> att =
                        metadataMap.get(String.class, attributeMapping.getGeoserver());
                att.setValue(node.getNodeValue());

            } else {
                int currentSize = metadataMap.size(attributeMapping.getGeoserver());
                ComplexMetadataAttribute<String> att =
                        metadataMap.get(String.class, attributeMapping.getGeoserver(), currentSize);
                att.setValue(node.getNodeValue());
            }
        }
    }

    /*private Document readXmlMetadata(Resource resource) throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(resource.in());
            doc.getDocumentElement().normalize();
            return doc;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        throw new IOException("Could not read resource:" + resource);
    }*/

    private NodeList findNode(Document doc, String geonetwork, Node node) {
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            xpath.setNamespaceContext(new NamespaceResolver(doc));
            XPathExpression expr = xpath.compile(geonetwork);
            Object result;
            if (node != null) {
                result = expr.evaluate(node, XPathConstants.NODESET);
            } else {
                result = expr.evaluate(doc, XPathConstants.NODESET);
            }
            NodeList nodes = (NodeList) result;
            /* for (int i = 0; i < nodes.getLength(); i++) {
                System.out.println(nodes.item(i).getTextContent());
            }*/
            return nodes;
        } catch (XPathExpressionException e) {

        }
        return null;
    }

    public class NamespaceResolver implements NamespaceContext {
        // Store the source document to search the namespaces
        private Document sourceDocument;

        public NamespaceResolver(Document document) {
            sourceDocument = document;
        }

        // The lookup for the namespace uris is delegated to the stored document.
        public String getNamespaceURI(String prefix) {
            if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                return sourceDocument.lookupNamespaceURI(null);
            } else {
                return sourceDocument.lookupNamespaceURI(prefix);
            }
        }

        public String getPrefix(String namespaceURI) {
            return sourceDocument.lookupPrefix(namespaceURI);
        }

        @SuppressWarnings("rawtypes")
        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }
    }
}
