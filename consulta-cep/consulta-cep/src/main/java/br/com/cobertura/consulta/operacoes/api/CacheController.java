package br.com.cobertura.consulta.operacoes.api;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/operacoes/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheManager cacheManager;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        var lista = cacheManager.getCacheNames().stream()
                .map(name -> {
                    var cache = cacheManager.getCache(name);
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("nome", name);
                    if (cache instanceof CaffeineCache caffeineCache) {
                        Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                        CacheStats stats = nativeCache.stats();
                        map.put("tamanho", nativeCache.estimatedSize());
                        map.put("hits", stats.hitCount());
                        map.put("misses", stats.missCount());
                        map.put("hitRate", String.format("%.2f%%", stats.hitRate() * 100));
                    }
                    return map;
                })
                .toList();
        return ResponseEntity.ok(lista);
    }
}
