package net.advanceteam.proxy.common.scheduler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.advanceteam.proxy.AdvanceProxy;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class ProxyScheduler implements Runnable {

    @Getter
    private final String identifier;

    /**
     * Если инлификатор в конструкторе не
     * указан, то он будет установлен на рандомные символы,
     * благодаря библиотеке org.apache.commons.lang3
     */
    public ProxyScheduler() {
        this( RandomStringUtils.randomAlphabetic(64) );
    }


    /**
     * Отмена и закрытие потока
     */
    public void cancel() {
        AdvanceProxy.getInstance().getSchedulerManager().cancelScheduler(identifier);
    }

    /**
     * Запустить асинхронный поток
     */
    public void runAsync() {
        AdvanceProxy.getInstance().getSchedulerManager().runAsync(this);
    }

    /**
     * Запустить поток через определенное
     * количество времени
     *
     * @param delay - время
     * @param timeUnit - единица времени
     */
    public void runLater(long delay, TimeUnit timeUnit) {
        AdvanceProxy.getInstance().getSchedulerManager().runLater(identifier, this, delay, timeUnit);
    }

    /**
     * Запустить цикличный поток через
     * определенное количество времени
     *
     * @param delay - время
     * @param period - период цикличного воспроизведения
     * @param timeUnit - единица времени
     */
    public void runTimer(long delay, long period, TimeUnit timeUnit) {
        AdvanceProxy.getInstance().getSchedulerManager().runTimer(identifier, this, delay, period, timeUnit);
    }

}
