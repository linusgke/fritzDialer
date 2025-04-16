/* ********************************************************** /*
 * javaAVMTR064 - open source Java TR-064 API                 *
 * JFritz - pc call management
 *                                                            *
 * Copyright (C) 2015 JFritz contributors                     *
 * Copyright (C) 2015 Marin Pollmann <pollmann.m@gmail.com>   *
 *                                                            *
/* ********************************************************** */
package de.linusgke.fritzdialer.fritz.tr064;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@XmlRootElement (name="service")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
		"serviceType",
		"serviceId",
		"controlURL",
		"eventSubURL",
		"scpdurl"
})
public class ServiceDesc
{

    @XmlElement(name = "serviceType", required = true)
	protected String serviceType;

    @XmlElement(required = true)
	protected String serviceId;

    @XmlElement(required = true)
	protected String controlURL;

    @XmlElement(required = true)
	protected String eventSubURL;

    @XmlElement(name = "SCPDURL", required = true)
	protected String scpdurl;
}