/* ********************************************************** /*
 * javaAVMTR064 - open source Java TR-064 API                 *
 * JFritz - pc call management
 *                                                            *
 * Copyright (C) 2015 JFritz contributors                     *
 * Copyright (C) 2015 Marin Pollmann <pollmann.m@gmail.com>   *
 *                                                            *
/* ********************************************************** */
package de.linusgke.fritzdialer.fritz.tr064;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.InputStream;

public class JAXBUtilities {

	public static JAXBContext getContext() throws ParseException {
		try {

			return JAXBContext.newInstance(RootType.class, ScpdType.class, ActionType.class,
					AllowedValueRangeType.class, ArgumentType.class, DeviceDesc.class, ServiceDesc.class,
					IconType.class, SpecVersionType.class, StateVariableType.class

			);
		} catch (JAXBException e) {
			throw new ParseException(e);
		}
	}

	public static Object unmarshalInput(InputStream in) throws ParseException {
		try {
			JAXBContext context = getContext();
			javax.xml.bind.Unmarshaller um = context.createUnmarshaller();
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			NamespaceFilter inFilter = new NamespaceFilter(null, false);
			inFilter.setParent(reader);
			InputSource is = new InputSource(in);
			SAXSource source = new SAXSource(inFilter, is);
			return um.unmarshal(source);
		} catch (SAXException | JAXBException | ParserConfigurationException e) {
			throw new ParseException(e);
		}
	}

	// utility class
	private JAXBUtilities() {
	}
}