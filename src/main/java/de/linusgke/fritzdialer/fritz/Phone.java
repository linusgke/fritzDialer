package de.linusgke.fritzdialer.fritz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Phone {

    private int dialPort;
    private String name;

}
