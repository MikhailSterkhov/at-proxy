package net.advanceteam.proxy.connection.login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResult {

    private String id;
    private String name;
    private Property[] properties;

    @Data
    @AllArgsConstructor
    public static class Property {

        private String name;
        private String value;
        private String signature;
    }

}