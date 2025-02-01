package dev.onlooker.utils.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReleaseType {

    PUBLIC("Public"),
    BETA("Beta"),
    ALPHA("Alpha"),
    DEV("Developer");

    private final String name;

}
