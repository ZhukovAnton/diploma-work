package com.stanum.skrudzh.service.providers_meta;

import com.stanum.skrudzh.jpa.model.ProviderMeta;
import com.stanum.skrudzh.jpa.repository.ProvidersMetaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ProvidersMetaService {

    private final ProvidersMetaRepository providersMetaRepository;

    public List<String> getProviders(String prototypeKey) {
        log.info("Get provider codes by prototypeKey={}", prototypeKey);
        Optional<ProviderMeta> meta = providersMetaRepository.findByPrototypeKey(prototypeKey);
        List<String> result;
        if (meta.isEmpty()) {
            result = null;
        } else if (meta.get().getDisabled()) {
            result = null;
        } else if (meta.get().getProviderCodes() == null) {
            result = new ArrayList<>();
        } else {
            result = Arrays.asList(meta.get().getProviderCodes().split(","));
        }
        log.info("Return providerCodes={} for prototypeKey={}", result, prototypeKey);
        return result;
    }
}
