package net.advanceteam.proxy.common.event.impl;

import lombok.*;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.common.event.cancel.Cancellable;
import net.advanceteam.proxy.connection.player.Player;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class TabCompleteResponseEvent extends ProxyEvent implements Cancellable {

    private boolean cancelled;

    private final List<String> suggestions;
    private final Player player;
}
