/* ********************************************************** /*
 * javaAVMTR064 - open source Java TR-064 API                 *
 * JFritz - pc call management
 *                                                            *
 * Copyright (C) 2015 JFritz contributors                     *
 * Copyright (C) 2015 Marin Pollmann <pollmann.m@gmail.com>   *
 *                                                            *
/* ********************************************************** */
package de.linusgke.fritzdialer.fritz.tr064;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Service {

    private final ServiceDesc serviceXML;
    private final Map<String, Action> actions;

    public Service(ServiceDesc serviceXML, TR064Connection connection) throws IOException, ParseException, UnauthorizedException {
        this.serviceXML = serviceXML;
        actions = new HashMap<>();

        try (InputStream is = connection.getXMLIS(serviceXML.getScpdurl())) {
            ScpdType scpd = (ScpdType) JAXBUtilities.unmarshalInput(is);
            log.debug(scpd.toString());
            for (ActionType a : scpd.getActionList()) {
                actions.put(a.getName(), new Action(a, scpd.getServiceStateTable(), connection, this.serviceXML));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            InputStream is = connection.getXMLIS(serviceXML.getScpdurl());
            ScpdType scpd = (ScpdType) JAXBUtilities.unmarshalInput(is);
            log.debug("scpd {}", scpd.toString());
            for (ActionType a : scpd.getActionList()) {
                actions.put(a.getName(), new Action(a, scpd.getServiceStateTable(), connection, this.serviceXML));
            }
        }

    }

    public Map<String, Action> getActions() {
        return actions;
    }

    public Action getAction(String name) {
        return getActions().get(name);
    }

    @Override
    public String toString() {
        return serviceXML.getServiceType();
    }

}