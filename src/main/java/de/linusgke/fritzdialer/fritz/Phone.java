package de.linusgke.fritzdialer.fritz;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Phone {

    private int port;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
