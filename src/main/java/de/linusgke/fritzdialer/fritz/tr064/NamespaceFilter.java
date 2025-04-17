/* ********************************************************** /*
 * javaAVMTR064 - open source Java TR-064 API                 *
 * JFritz - pc call management
 *                                                            *
 * Copyright (C) 2015 JFritz contributors                     *
 * Copyright (C) 2015 Marin Pollmann <pollmann.m@gmail.com>   *
 *                                                            *
/* ********************************************************** */
package de.linusgke.fritzdialer.fritz.tr064;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.XMLFilterImpl;

public class NamespaceFilter extends XMLFilterImpl {

    private final String usedNamespaceUri;
    private final boolean addNamespace;
    private boolean addedNamespace = false;

    public NamespaceFilter(String namespaceUri, boolean addNamespace) {
        super();

        if (addNamespace) this.usedNamespaceUri = namespaceUri;
        else this.usedNamespaceUri = "";
        this.addNamespace = addNamespace;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        if (addNamespace) {
            startControlledPrefixMapping();
        }
    }

    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException {
        super.startElement(this.usedNamespaceUri, arg1, arg2, arg3);
    }

    @Override
    public void endElement(String arg0, String arg1, String arg2) throws SAXException {
        super.endElement(this.usedNamespaceUri, arg1, arg2);
    }

    @Override
    public void startPrefixMapping(String prefix, String url) throws SAXException {
        if (addNamespace) {
            this.startControlledPrefixMapping();
        } else {
            //Remove the namespace, i.e. don't call startPrefixMapping for parent!
        }
    }

    private void startControlledPrefixMapping() throws SAXException {
        if (this.addNamespace && !this.addedNamespace) {
            //We should add namespace since it is set and has not yet been done.
            super.startPrefixMapping("", this.usedNamespaceUri);

            //Make sure we don't do it twice
            this.addedNamespace = true;
        }
    }
}