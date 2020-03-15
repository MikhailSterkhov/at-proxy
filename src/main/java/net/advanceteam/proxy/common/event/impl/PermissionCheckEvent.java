package net.advanceteam.proxy.common.event.impl;

import lombok.*;
import net.advanceteam.proxy.common.command.CommandSender;
import net.advanceteam.proxy.common.event.ProxyEvent;

/**
 * Called when the permission of a CommandSender is checked.
 */
@AllArgsConstructor
public class PermissionCheckEvent extends ProxyEvent {

    @Getter
    private final CommandSender sender;

    @Getter
    private final String permission;


    private boolean hasPermission;

    public boolean hasPermission() {
        return hasPermission;
    }

}
