/* ********************************************************** /*
 * javaAVMTR064 - open source Java TR-064 API                 *
 * JFritz - pc call management
 *                                                            *
 * Copyright (C) 2015 JFritz contributors                     *
 * Copyright (C) 2015 Marin Pollmann <pollmann.m@gmail.com>   *
 *                                                            *
/* ********************************************************** */
package de.linusgke.fritzdialer.fritz.tr064;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

@Slf4j
public class Action {

	private final Map<String, Class<?>> stateToType;
	private final Map<String, Boolean> argumentOut;
	private final Map<String, String> argumentState;
	private final String name;
	private final FritzConnection connection;
	private final ServiceDesc serviceXML;

	public Action(ActionType action, List<StateVariableType> stateVariableList, FritzConnection connection,
			ServiceDesc serviceXML) {
		stateToType = new HashMap<>();
		argumentOut = new HashMap<>();
		argumentState = new HashMap<>();
		name = action.getName();
		this.connection = connection;
		this.serviceXML = serviceXML;
		for (StateVariableType s : stateVariableList) {
			Class<?> type = null;
			if ("string".equals(s.getDataType()))
				type = String.class;
			else if (s.getDataType().startsWith("ui") || s.getDataType().startsWith("i"))
				type = Integer.class;
			else if ("boolean".equals(s.getDataType()))
				type = Boolean.class;
			else if ("dateTime".equals(s.getDataType()))
				type = Date.class;
			else if ("uuid".equals(s.getDataType()))
				type = UUID.class;
			else {
				log.error("UNKNOWN TYPE: {} {} {}", s.getDataType(), s.getName(), action.getName());
			}
			stateToType.put(s.getName(), type);
		}
		if (action.getArgumentList() != null)
			for (ArgumentType a : action.getArgumentList()) {
				String argName = a.getName();
				argumentOut.put(argName, "out".equals(a.getDirection()));
				argumentState.put(argName, a.getRelatedStateVariable());
			}
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder(getName() + ":");
		for (Map.Entry<String, Boolean> entry : argumentOut.entrySet()) {
			ret.append(" ").append(entry.getKey());
			ret.append(entry.getValue() ? " out" : " in");
			ret.append(" ").append(stateToType.get(argumentState.get(entry.getKey())).getName());
		}

		return ret.toString();
	}

	public String getName() {
		return name;
	}

	public Set<String> getArguments() {
		return argumentOut.keySet();
	}

	public boolean isOut(String argument) {
		if (!argumentOut.containsKey(argument))
			throw new UnsupportedOperationException(argument);
		return argumentOut.get(argument);
	}

	public boolean isIn(String argument) {
		return !isOut(argument);
	}

	public Class<?> getTypeOfArgument(String argument) {
		if (!argumentOut.containsKey(argument))
			throw new UnsupportedOperationException(argument);
		return stateToType.get(argumentState.get(argument));
	}

	public CompletableFuture<Response> executeAsync() {
		return executeAsync(null);
	}

	public CompletableFuture<Response> executeAsync(Map<String, Object> arguments) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return execute(arguments);
			} catch (IOException | UnauthorizedException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public Response execute() throws IOException, UnauthorizedException {
		return this.execute(null);
	}

	public Response execute(Map<String, Object> arguments) throws IOException, UnauthorizedException {
		Set<String> inArguments = new HashSet<>();
		for (Map.Entry<String, Boolean> entry : argumentOut.entrySet()) {
			if (!entry.getValue()) {
				inArguments.add(entry.getKey());
			}
		}
		if (arguments != null) {
			for (String a : arguments.keySet()) {
				Object parameter = arguments.get(a);
				if (!argumentOut.containsKey(a))
					throw new UnsupportedOperationException(a + " is not an Argument for " + this.name);
				if (argumentOut.get(a))
					throw new UnsupportedOperationException(a + " is not In");
				if (parameter.getClass() != this.getTypeOfArgument(a)) {
					try {
						parameter = Class.forName(this.getTypeOfArgument(a).getName()).cast(parameter);
					} catch (Exception e) {
						throw new UnsupportedOperationException(a + " has to be " + this.getTypeOfArgument(a)
								+ ". Cast not possible: " + e.getMessage(), e);
					}

				}
				inArguments.remove(a);
			}

		}
		if (!inArguments.isEmpty())
			throw new UnsupportedOperationException("Missing In-Arguments: " + inArguments);
		HttpEntity httpEntity;
		String message = null;
		try {
			MessageFactory factory = MessageFactory.newInstance();
			SOAPMessage soapMsg = factory.createMessage();
			SOAPPart part = soapMsg.getSOAPPart();
			SOAPEnvelope envelope = part.getEnvelope();
			SOAPBody body = envelope.getBody();
			envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
			SOAPBodyElement element = body.addBodyElement(envelope.createName(this.name, "u",
					serviceXML.getServiceType()));
			if (arguments != null) {
				for (String key : arguments.keySet()) {
					element.addChildElement(key).addTextNode(valueToSOAPString(key, arguments.get(key)));
				}
			}
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapMsg.writeTo(stream);
			message = new String(stream.toByteArray(), StandardCharsets.UTF_8);
		} catch (SOAPException e) {
			log.error(e.getMessage(), e);
		}

		httpEntity = new StringEntity(message);

		return getMessage(connection.getSOAPXMLIS(serviceXML.getControlURL(),
				serviceXML.getServiceType() + "#" + this.getName(), httpEntity));
	}

	private String valueToSOAPString(String name, Object value) {
		Class<?> classOfValue = null;
		String ret="";
		try {
			classOfValue = Class.forName(this.getTypeOfArgument(name).getName());
		} catch (ClassNotFoundException | UnsupportedOperationException e) {
			// Cant happen already checked
			log.error(e.getMessage(), e);
		}

		if (classOfValue == String.class) {
			ret =  (String) value;
		}

		else if (classOfValue == Integer.class) {
			int tmp = (Integer) value;
			ret = Integer.toString(tmp);

		}

		else if (classOfValue == Boolean.class) {
			boolean tmp = (Boolean) value;
			ret = tmp ? "1" : "0";

		}

		else if (classOfValue == Date.class) {
			Date tmp = (Date) value;
			String dateFormat = "yyyy-MM-dd'T'hh:mm:ss'Z'";
			DateFormat lDateFormat = new SimpleDateFormat(dateFormat);
			ret = lDateFormat.format(tmp);
		}

		else if (classOfValue == UUID.class) {
			UUID tmp = (UUID) value;
			ret = tmp.toString();
		}

		return ret;
	}

	private Response getMessage(InputStream soapXmlInputStream) throws IOException {
		SOAPMessage response;
		Response ret;

		try {
			response = MessageFactory.newInstance().createMessage(null, soapXmlInputStream);
		} catch (IOException | SOAPException e) {
			throw new IOException(e);
		}

		if (response == null)
			return null;

		try {
			ret = new Response(response, stateToType, argumentState);
		} catch (SOAPException e) {
			throw new IOException(e);
		}
		return ret;

	}

}