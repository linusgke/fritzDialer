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
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@XmlRootElement (name="argument")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "argumentType", propOrder = {
    "name",
    "direction",
    "relatedStateVariable"
})
public class ArgumentType {

    @XmlElement(required = true)
    protected String name;

    @XmlElement(required = true)
    protected String direction;

    @XmlElement(required = true)
    protected String relatedStateVariable;

}