package net.advanceteam.proxy.common.event.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.advanceteam.proxy.common.command.CommandSender;
import net.advanceteam.proxy.common.event.ProxyEvent;

@Getter
@RequiredArgsConstructor
public class ProxyReloadEvent extends ProxyEvent {

    private final CommandSender sender;
}
