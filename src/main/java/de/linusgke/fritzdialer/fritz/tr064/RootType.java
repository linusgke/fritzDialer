/* ********************************************************** /*
 * javaAVMTR064 - open source Java TR-064 API                 *
 * JFritz - pc call management
 *                                                            *
 * Copyright (C) 2015 JFritz contributors                     *
 * Copyright (C) 2015 Marin Pollmann <pollmann.m@gmail.com>   *
 *                                                            *
/* ********************************************************** */
package de.linusgke.fritzdialer.fritz.tr064;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Setter
@Getter
@ToString
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rootType", propOrder = {"specVersion", "device"})
public class RootType {

    @XmlElement(name = "specVersion", required = true)
    private SpecVersionType specVersion;

    @XmlElement(name = "device", required = true)
    private DeviceDesc device;

}