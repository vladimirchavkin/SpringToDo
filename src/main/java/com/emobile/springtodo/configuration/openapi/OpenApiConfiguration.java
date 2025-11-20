package com.emobile.springtodo.configuration.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурационный класс для настройки OpenAPI (Swagger).
 * Создаёт bean {@link OpenAPI} с информацией о API и списком серверов на основе
 * свойств, заданных в {@link OpenApiConfigurationProperties}.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties
public class OpenApiConfiguration {

    /**
     * Свойства конфигурации OpenAPI, содержащие информацию о заголовке, версии, описании и серверах.
     */
    private final OpenApiConfigurationProperties openApiConfigurationProperties;

    /**
     * Создаёт bean {@link OpenAPI} для настройки документации API.
     *
     * @return объект {@link OpenAPI} с информацией и списком серверов
     */
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(getInfoForOpenApi())
                .servers(getServersForOpenApi());
    }

    /**
     * Формирует информацию об API для OpenAPI.
     * Включает заголовок, версию, описание, контактные данные, лицензию и условия использования.
     *
     * @return объект {@link Info} с метаданными API
     */
    public Info getInfoForOpenApi() {
        final OpenApiConfigurationProperties.InfoConfig infoConfig = openApiConfigurationProperties.getInfo();
        return new Info()
                .title(infoConfig.getTitle())
                .version(infoConfig.getVersion())
                .contact(getContactForOpenApi())
                .description(infoConfig.getDescription())
                .license(getLicenseForOpenApi())
                .termsOfService("https://example.com/terms");
    }

    /**
     * Формирует контактные данные для OpenAPI.
     *
     * @return объект {@link Contact} с именем, email и URL
     */
    private Contact getContactForOpenApi() {
        return new Contact()
                .name("Vladimir Chavkin")
                .email("vladimirchavkinwork@gmail.com")
                .url("https://github.com/vladimirchavkin");
    }

    /**
     * Формирует лицензию для OpenAPI.
     *
     * @return объект {@link License} с названием и URL лицензии
     */
    private License getLicenseForOpenApi() {
        return new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");
    }

    /**
     * Формирует список серверов для OpenAPI на основе конфигурации.
     *
     * @return список объектов {@link Server}, представляющих серверы API
     */
    private List<Server> getServersForOpenApi() {
        return openApiConfigurationProperties.getServers().stream()
                .map(this::getServer)
                .toList();
    }

    /**
     * Преобразует конфигурацию сервера в объект {@link Server} для OpenAPI.
     *
     * @param serverConfig конфигурация сервера из {@link OpenApiConfigurationProperties.ServerConfig}
     * @return объект {@link Server} с URL и описанием
     */
    private Server getServer(final OpenApiConfigurationProperties.ServerConfig serverConfig) {
        return new Server()
                .url(serverConfig.getUrl())
                .description(serverConfig.getDescription());
    }
}