package net.advanceteam.proxy.netty.protocol.client.version;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClientVersion {

    //--1.8--//
    V1_8(47),
    V1_8_1(47),
    V1_8_2(47),
    V1_8_3(47),
    V1_8_4(47),
    V1_8_5(47),
    V1_8_6(47),
    V1_8_7(47),
    V1_8_8(47),
    V1_8_9(47),

    //--1.9--//
    V1_9(107),
    V1_9_1(108),
    V1_9_2(109),
    V1_9_3(110),
    V1_9_4(110),

    //--1.10--//
    V1_10(210),
    V1_10_1(210),
    V1_10_2(210),

    //--1.11--//
    V1_11(315),
    V1_11_1(316),
    V1_11_2(360),

    //--1.12--//
    V1_12(335),
    V1_12_1(338),
    V1_12_2(340),

    //--1.13--//
    V1_13(393),
    V1_13_1(401),
    V1_13_2(404),

    //--1.14--//
    V1_14(477),
    V1_14_1(480),
    V1_14_2(485),
    V1_14_3(490),
    V1_14_4(498),

    //--1.15--//
    V1_15(573),
    V1_15_1(575),
    V1_15_2(578);

    private int version; //версия клиента


    public static ClientVersion getVersion(int versionId) {
        for (ClientVersion clientVersion : ClientVersion.values()) {
            if ( clientVersion.version != versionId ) {
                continue;
            }

            return clientVersion;
        }

        return null;
    }

}
