/* (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.metadata.data.service.impl;


import org.geoserver.catalog.MetadataMap;
import org.geoserver.metadata.data.dto.AttributeComplexTypeMapping;
import org.geoserver.metadata.data.dto.AttributeMapping;
import org.geoserver.metadata.data.dto.AttributeMappingConfiguration;
import org.geoserver.metadata.data.dto.FieldTypeEnum;
import org.geoserver.metadata.data.dto.OccurenceEnum;
import org.geoserver.metadata.data.service.ImportGeonetworkMetadataService;
import org.geoserver.metadata.data.service.YamlService;
import org.geotools.util.logging.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class ImportGeonetworkMetadataServiceImpl implements ImportGeonetworkMetadataService {

    private static final java.util.logging.Logger LOGGER = Logging.getLogger(ImportGeonetworkMetadataServiceImpl.class);

    @Autowired
    private YamlService yamlService;


    @Override
    public MetadataMap importMetadata(String url, MetadataMap metadataMap) throws IOException {
        Document doc = readXmlMetadata(url);
        /*if (doc.hasChildNodes()) {
            printNote(doc.getChildNodes());
        }
*/
        AttributeMappingConfiguration mapping = yamlService.readMapping();

        for (AttributeMapping attributeMapping : mapping.getGeonetworkmapping()) {
            addAttribute(metadataMap, attributeMapping, doc, null, mapping.getObjectmapping());
        }


        return metadataMap;
    }

    private void addAttribute(MetadataMap metadataMap, AttributeMapping attributeMapping, Document doc, Node node, List<AttributeComplexTypeMapping> mapping) {
        NodeList nodes = findNode(doc, attributeMapping.getGeonetwork(), node);

        if (nodes != null && nodes.getLength() > 0) {
            switch (attributeMapping.getOccurrence()) {
                case SINGLE:
                    mapNode(metadataMap, attributeMapping, doc, nodes.item(0), mapping);
                    //metadataMap.put(attributeMapping.getGeoserver(), nodes.item(0).getNodeValue());
                    break;
                case REPEAT:
                    for (int count = 0; count < nodes.getLength(); count++) {
                        mapNode(metadataMap, attributeMapping, doc, nodes.item(count), mapping);
                        //metadataMap.put(attributeMapping.getGeoserver(), nodes.item(count).getNodeValue());
                    }
                    break;
            }
        }


    }

    @SuppressWarnings("unchecked")
    private void mapNode(MetadataMap metadataMap, AttributeMapping attributeMapping, Document doc, Node node, List<AttributeComplexTypeMapping> mapping) {
        if (FieldTypeEnum.COMPLEX.equals(attributeMapping.getFieldType())) {
            for (AttributeComplexTypeMapping complexTypeMapping : mapping) {
                if (attributeMapping.getTypename().equals(complexTypeMapping.getTypename())) {
                    for (AttributeMapping aMapping : complexTypeMapping.getMapping()) {
                        AttributeMapping am = new AttributeMapping(aMapping);
                        am.setOccurrence(attributeMapping.getOccurrence());
                        am.setGeoserver(attributeMapping.getGeoserver() + "_" + am.getGeoserver());
                        addAttribute(metadataMap, am, doc, node, mapping);
                    }
                    break;
                }
            }
        } else {
            if (OccurenceEnum.SINGLE.equals(attributeMapping.getOccurrence())) {
                metadataMap.put(attributeMapping.getGeoserver(), node.getNodeValue());
            } else {
                if(!metadataMap.containsKey(attributeMapping.getGeoserver())){
                    metadataMap.put(attributeMapping.getGeoserver(), new ArrayList<>());
                }
                ((List<String>) metadataMap.get(attributeMapping.getGeoserver())).add(node.getNodeValue());

            }
        }
    }


    private Document readXmlMetadata(String geonetworkUrl) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            URL url = new URL(geonetworkUrl);
            InputStream stream = url.openStream();
            //System.out.println(IOUtils.toString(stream));
            //Document doc = builder.parse(new FileInputStream(new File("sample.xml")));
            Document doc = db.parse(stream);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


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
            for (int i = 0; i < nodes.getLength(); i++) {
                System.out.println(nodes.item(i).getTextContent());
            }
            return nodes;
        } catch (XPathExpressionException e) {

        }
        return null;
    }


    public class NamespaceResolver implements NamespaceContext {
        //Store the source document to search the namespaces
        private Document sourceDocument;

        public NamespaceResolver(Document document) {
            sourceDocument = document;
        }

        //The lookup for the namespace uris is delegated to the stored document.
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
