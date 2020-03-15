package net.advanceteam.proxy.common.event.impl;

import io.netty.channel.Channel;
import lombok.*;
import net.advanceteam.proxy.common.chat.component.BaseComponent;
import net.advanceteam.proxy.common.chat.component.TextComponent;
import net.advanceteam.proxy.common.event.ProxyEvent;
import net.advanceteam.proxy.common.event.cancel.Cancellable;

@RequiredArgsConstructor
@Getter
@Setter
public class PreLoginEvent extends ProxyEvent implements Cancellable {

    private boolean cancelled;

    private BaseComponent[] cancelReasonComponents;

    private final Channel connection;
    private final String playerName;


    public String getCancelReason() {
        return BaseComponent.toLegacyText(getCancelReasonComponents());
    }

    public void setCancelReason(String cancelReason) {
        setCancelReason(TextComponent.fromLegacyText(cancelReason));
    }

    public void setCancelReason(BaseComponent... cancelReason) {
        this.cancelReasonComponents = cancelReason;
    }
}
