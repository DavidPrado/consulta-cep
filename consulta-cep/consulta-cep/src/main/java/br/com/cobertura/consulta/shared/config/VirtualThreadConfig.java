package br.com.cobertura.consulta.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class VirtualThreadConfig {

    @Bean(name = "loteExecutor", destroyMethod = "close")
    public ExecutorService loteExecutor() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        log.info("VirtualThreadConfig: ExecutorService de Virtual Threads inicializado");
        return executor;
    }
}