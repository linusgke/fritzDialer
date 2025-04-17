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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Getter
@Setter
@ToString
@XmlRootElement(name = "scpd")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scpdType", propOrder = {"specVersion", "actionList", "serviceStateTable"})
public class ScpdType {

    @XmlElement(required = true)
    protected SpecVersionType specVersion;

    @XmlElementWrapper(name = "actionList", required = true)
    @XmlElement(name = "action")
    private List<ActionType> actionList;

    @XmlElementWrapper(name = "serviceStateTable")
    @XmlAnyElement(lax = true)
    protected List<StateVariableType> serviceStateTable;

    public List<StateVariableType> getServiceStateTable() {
        if (this.serviceStateTable == null) {
            this.serviceStateTable = new ArrayList<>();
        }
        return serviceStateTable;
    }

    public List<ActionType> getActionList() {
        if (this.actionList == null) {
            this.actionList = new ArrayList<>();
        }
        return actionList;
    }

}