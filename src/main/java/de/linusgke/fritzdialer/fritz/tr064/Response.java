/* ********************************************************** /*
 * javaAVMTR064 - open source Java TR-064 API                 *
 * JFritz - pc call management
 *                                                            *
 * Copyright (C) 2015 JFritz contributors                     *
 * Copyright (C) 2015 Marin Pollmann <pollmann.m@gmail.com>   *
 *                                                            *
/* ********************************************************** */
package de.linusgke.fritzdialer.fritz.tr064;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.NodeList;

public class Response {

	private final SOAPMessage response;
	private final Map<String, Class<?>> stateToType;
	private final Map<String, String> argumentState;
	private final Map<String, String> data;

	public Response(SOAPMessage response, Map<String, Class<?>> stateToType, Map<String, String> argumentState)
			throws SOAPException {
		this.response = response;
		this.stateToType = stateToType;
		this.argumentState = argumentState;
		this.data = new HashMap<>();

		NodeList nodes = null;
		NodeList tmp = response.getSOAPBody().getChildNodes();
		for (int i = 1; i < tmp.getLength() && nodes == null; i++) {
			if ("#text".equals(tmp.item(i).getNodeName()))
				continue;
			nodes = tmp.item(i).getChildNodes();
		}

		if (nodes != null) {
			for (int i = 1; i < nodes.getLength(); i++) {
				if ("#text".equals(nodes.item(i).getNodeName()))
					continue;
				data.put(nodes.item(i).getNodeName(), nodes.item(i).getTextContent());
			}
		}
	}

	public SOAPMessage getSOAPMessage() {
		return response;
	}
	
	public Map<String, String> getData() {
		return data;
	}

	public String getValueAsString(String argument) throws NoSuchFieldException {
		if (!argumentState.containsKey(argument) || !data.containsKey(argument))
			throw new NoSuchFieldException(argument);
		return data.get(argument);
	}

	public int getValueAsInteger(String argument) throws ClassCastException, NoSuchFieldException {
		if (!argumentState.containsKey(argument) || !data.containsKey(argument))
			throw new NoSuchFieldException(argument);
		if (stateToType.get(argumentState.get(argument)) != Integer.class)
			throw new ClassCastException(argument);

		int ret;
		try {
			ret = Integer.parseInt(data.get(argument));
		} catch (NumberFormatException e) {
			throw new ClassCastException(argument + " " + e.getMessage());
		}

		return ret;
	}

	public boolean getValueAsBoolean(String argument) throws ClassCastException, NoSuchFieldException {
		if (!argumentState.containsKey(argument) || !data.containsKey(argument))
			throw new NoSuchFieldException(argument);
		if (stateToType.get(argumentState.get(argument)) != Boolean.class)
			throw new ClassCastException(argument);

		boolean ret;

		if ("1".equals(data.get(argument)) || "true".equalsIgnoreCase(data.get(argument)))
			ret = true;
		else if ("0".equals(data.get(argument)) || "false".equalsIgnoreCase(data.get(argument)))
			ret = false;
		else
			throw new ClassCastException(argument);

		return ret;
	}

	public Date getValueAsDate(String argument) throws ClassCastException, NoSuchFieldException {
		if (!argumentState.containsKey(argument) || !data.containsKey(argument))
			throw new NoSuchFieldException(argument);
		if (stateToType.get(argumentState.get(argument)) != Date.class)
			throw new ClassCastException(argument);

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

		Date ret;
		try {
			ret = dateFormat.parse(data.get(argument));
		} catch (ParseException e) {
			throw new ClassCastException(argument + " " + e.getMessage());
		}

		return ret;
	}

	public UUID getValueAsUUID(String argument) throws NoSuchFieldException {
		if (!argumentState.containsKey(argument) || !data.containsKey(argument))
			throw new NoSuchFieldException(argument);
		if (stateToType.get(argumentState.get(argument)) != UUID.class)
			throw new ClassCastException(argument);
		
		return UUID.fromString(data.get(argument));
	}

}