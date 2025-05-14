package com.santander.springcepapi.service.event.subscriber;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CepSubscriberManager {

    private static final Logger LOG = LoggerFactory.getLogger(CepSubscriberManager.class);
    private final Set<CepSubscription> globalSubscribers = ConcurrentHashMap.newKeySet();
    private final Map<String, Set<CepSubscription>> cepSubscribers = new ConcurrentHashMap<>();

    public void registrarSubscriberEventos(Subscription subscription) {
        String subscriptionId = UUID.randomUUID().toString();
        CepSubscription info = new CepSubscription(
                subscriptionId,
                "global",
                subscription,
                LocalDateTime.now()
        );
        globalSubscribers.add(info);
        subscription.request(Long.MAX_VALUE);

        LOG.info("Novo subscriber global registrado - ID: {}", subscriptionId);
    }

    public void registrarSubscriberEventosCep(String cep, Subscription subscription) {
        String subscriptionId = UUID.randomUUID().toString();
        CepSubscription info = new CepSubscription(
                subscriptionId,
                cep,
                subscription,
                LocalDateTime.now()
        );
        cepSubscribers.computeIfAbsent(cep, _ -> ConcurrentHashMap.newKeySet()).add(info);
        subscription.request(Long.MAX_VALUE);

        LOG.info("Novo subscriber registrado para CEP {} - ID: {}", cep, subscriptionId);
    }

    @Scheduled(fixedRate = 3600000)
    public void limparSubscribersInativos() {
        cepSubscribers.forEach((cep, subscriptions) -> subscriptions.removeIf(info -> {
            if (ChronoUnit.HOURS.between(info.subscriptionTime(), LocalDateTime.now()) > 24) {
                info.subscription().cancel();
                LOG.info("Subscriber inativo removido para CEP {} - ID: {}",
                        cep, info.subscriptionId());
                return true;
            }
            return false;
        }));
    }

    record CepSubscription(
            String subscriptionId,
            String cep,
            Subscription subscription,
            LocalDateTime subscriptionTime
    ) {
    }

}
