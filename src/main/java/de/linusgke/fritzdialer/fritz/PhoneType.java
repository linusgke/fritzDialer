package de.linusgke.fritzdialer.fritz;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PhoneType {

    ANALOG("telcfg:settings/MSN/Port/count", "telcfg:settings/MSN/Port%s/Name", 0),
    ISDN("telcfg:settings/NTHotDialList/Name/count", "telcfg:settings/NTHotDialList/Name%s", 50),
    DECT("telcfg:settings/Foncontrol/User/count", "telcfg:settings/Foncontrol/User%s/Name", 609),
    VOIP("telcfg:settings/VoipExtension/count", "telcfg:settings/VoipExtension%s/Name", 620);

    private final String countQuery;
    private final String nameQuery;
    private final int firstDialPort;

}
