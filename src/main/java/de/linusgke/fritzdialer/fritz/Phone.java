package de.linusgke.fritzdialer.fritz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
public class Phone {

    private int dialPort;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
