/* ********************************************************** /*
 * javaAVMTR064 - open source Java TR-064 API                 *
 * JFritz - pc call management
 *                                                            *
 * Copyright (C) 2015 JFritz contributors                     *
 * Copyright (C) 2015 Marin Pollmann <pollmann.m@gmail.com>   *
 *                                                            *
/* ********************************************************** */
package de.linusgke.fritzdialer.fritz.tr064;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@XmlRootElement (name = "stateVariable")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stateVariableType", propOrder = {
    "name",
    "dataType",
    "allowedValueList",
    "allowedValueRange",
    "defaultValue"
})
public class StateVariableType {


    @XmlAttribute(name = "sendEvents")
    protected String sendEvents;

    @XmlElement(name="name", required = true)
    protected String name;

    @XmlElement(required = true)
    protected String dataType;

    @XmlElementWrapper(name="allowedValueList")
    @XmlAnyElement (lax = true)
    private List<String> allowedValueList;

    private AllowedValueRangeType allowedValueRange;

    private String defaultValue;

    /**
	 * @return the allowedValueList
	 */
	public List<String> getAllowedValueList()
	{
		if( this.allowedValueList == null ) {
			this.allowedValueList = new ArrayList<>();
		}
		return allowedValueList;
	}
}