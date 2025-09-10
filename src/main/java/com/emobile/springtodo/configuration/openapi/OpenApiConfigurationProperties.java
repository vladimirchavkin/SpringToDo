package com.emobile.springtodo.configuration.openapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурационный класс для настройки свойств OpenAPI (Swagger).
 * Содержит параметры для описания API (информация, серверы) и связывается с
 * префиксом конфигурации {@value #PREFIX} в файле настроек приложения.
 */
@Data
@Configuration
@ConfigurationProperties(OpenApiConfigurationProperties.PREFIX)
public class OpenApiConfigurationProperties {

    /**
     * Префикс для свойств конфигурации OpenAPI в файле настроек (например, application.yml).
     */
    public static final String PREFIX = "springdoc";

    /**
     * Конфигурация информации об API, включая заголовок, версию и описание.
     */
    private InfoConfig info;

    /**
     * Список конфигураций серверов, доступных для API.
     */
    private List<ServerConfig> servers;

    /**
     * Внутренний класс для хранения информации об API.
     * Содержит заголовок, версию и описание API для отображения в документации OpenAPI.
     */
    @Data
    public static final class InfoConfig {
        /**
         * Заголовок API, отображаемый в документации OpenAPI.
         */
        private String title;

        /**
         * Версия API, указанная в документации OpenAPI.
         */
        private String version;

        /**
         * Описание API, отображаемое в документации OpenAPI.
         */
        private String description;
    }

    /**
     * Внутренний класс для хранения конфигурации серверов API.
     * Содержит URL и описание сервера для отображения в документации OpenAPI.
     */
    @Data
    public static final class ServerConfig {
        /**
         * URL сервера, используемого для доступа к API.
         */
        private String url;

        /**
         * Описание сервера, отображаемое в документации OpenAPI.
         */
        private String description;
    }
}