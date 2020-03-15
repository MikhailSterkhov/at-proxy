package net.advanceteam.proxy.common.ping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatusResponseCallback {

    private Version version;
    private Players players;

    private Description description;

    private String favicon;

    @Getter
    @AllArgsConstructor
    public static class Players {

        private int max;
        private int online;

        private Player[] sample;
    }

    @Getter
    @AllArgsConstructor
    public static class Description {

        private String text;
    }

    @Getter
    @AllArgsConstructor
    private static class Player {

        private String name;
        private String id;
    }

    @Getter
    @AllArgsConstructor
    public static class Version {

        private String name;
        private int protocol;

    }
}

