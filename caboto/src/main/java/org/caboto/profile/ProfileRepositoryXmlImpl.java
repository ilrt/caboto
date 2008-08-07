/*
 * Copyright (c) 2008, University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the name of the University of Bristol nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.caboto.profile;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: ProfileRepositoryXmlImpl.java 177 2008-05-30 13:50:59Z mike.a.jones $
 *
 **/
public final class ProfileRepositoryXmlImpl implements ProfileRepository {

    public ProfileRepositoryXmlImpl(final String xmlFileName) throws ProfileRepositoryException {

        try {
            loadXmlDocument(xmlFileName);
        } catch (ParserConfigurationException e) {
            throw new ProfileRepositoryException(e.getMessage());
        } catch (SAXException e) {
            throw new ProfileRepositoryException(e.getMessage());
        } catch (IOException e) {
            throw new ProfileRepositoryException(e.getMessage());
        }

    }

    private void loadXmlDocument(final String xmlFileName) throws ParserConfigurationException,
            IOException, SAXException {
        String xmlFileNamePath = getClass().getClassLoader().getResource(xmlFileName).getPath();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(xmlFileNamePath);
    }


    public Profile findProfile(final String profileId) throws ProfileRepositoryException {

        Profile profile = null;

        if (profileId == null) {

            // ????????????????????????
        }

        try {

            // find the profile matching the id
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expression =
                    xpath.compile("//profile[@id=\"" + profileId + "\"]");
            Object result = expression.evaluate(document, XPathConstants.NODE);

            // get the profile
            Node node = (Node) result;

            // find the rdf type
            NamedNodeMap attributes = node.getAttributes();
            String rdfType = attributes.getNamedItem("type").getTextContent();

            // get the child nodes
            NodeList list = node.getChildNodes();

            // temp container to hold profile entries
            List<Node> profileEntryList = new ArrayList<Node>();

            for (int i = 0; i < list.getLength(); i++) {

                Node n = list.item(i);

                if (n.getNodeName().equals("profileEntry")) {
                    profileEntryList.add(n);
                }

            }

            // create the profile object if we have entries
            if (profileEntryList.size() > 0) {

                // create the profile object
                profile = new Profile();
                profile.setId(profileId);
                profile.setType(rdfType);

                for (Node n : profileEntryList) {
                    profile.getProfileEntries().add(getProfileEntry(n));
                }
            }


        } catch (XPathExpressionException e) {
            throw new ProfileRepositoryException(e.getMessage());
        }

        return profile;
    }

    private ProfileEntry getProfileEntry(final Node node) {

        ProfileEntry entry = new ProfileEntry();

        NamedNodeMap nodeAttributes = node.getAttributes();

        entry.setId(nodeAttributes.getNamedItem("id").getTextContent());
        entry.setPropertyType(nodeAttributes.getNamedItem("propertyType").getTextContent());
        entry.setObjectDatatype(nodeAttributes.getNamedItem("objectDatatype")
                .getTextContent());

        if (nodeAttributes.getNamedItem("required").getTextContent().equals("true")) {
            entry.setRequired(true);
        } else {
            entry.setRequired(false);
        }

        return entry;
    }


    private Document document;
}
