/* ********************************************************** /*
 * javaAVMTR064 - open source Java TR-064 API                 *
 * JFritz - pc call management
 *                                                            *
 * Copyright (C) 2015 JFritz contributors                     *
 * Copyright (C) 2015 Marin Pollmann <pollmann.m@gmail.com>   *
 *                                                            *
/* ********************************************************** */
package de.linusgke.fritzdialer.fritz.tr064;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlRootElement(name = "device")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "device", propOrder = {"deviceType", "friendlyName", "manufacturer", "manufacturerURL", "modelDescription", "modelName", "modelNumber", "modelURL", "udn", "upc", "iconList", "serviceList", "deviceList", "presentationURL"})
public class DeviceDesc {

    @XmlElement(required = true)
    private String deviceType;
    @XmlElement(required = true)
    private String friendlyName;
    @XmlElement(required = true)
    private String manufacturer;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String manufacturerURL;
    @XmlElement(required = true)
    private String modelDescription;
    @XmlElement(required = true)
    private String modelName;
    @XmlElement(required = true)
    private String modelNumber;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String modelURL;
    @XmlElement(name = "UDN", required = true)
    private String udn;
    @XmlElement(name = "UPC")
    private String upc;

    @XmlElementWrapper(name = "iconList")
    @XmlAnyElement(lax = true)
    private List<IconType> iconList;

    @XmlElementWrapper(name = "serviceList", required = true)
    @XmlAnyElement(lax = true)
    private List<ServiceDesc> serviceList;

    @XmlElementWrapper(name = "deviceList")
    @XmlAnyElement(lax = true)
    private List<DeviceDesc> deviceList;

    @XmlSchemaType(name = "anyURI")
    private String presentationURL;

}