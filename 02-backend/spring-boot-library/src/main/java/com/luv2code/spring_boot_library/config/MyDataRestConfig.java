package com.luv2code.spring_boot_library.config;

import com.luv2code.spring_boot_library.entity.Book;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {

    private static final String REACT_APP = "http://localhost:3000";

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

        HttpMethod[] persistingActions = {
                HttpMethod.POST,
                HttpMethod.PUT,
                HttpMethod.PATCH,
                HttpMethod.DELETE
        };

        config.exposeIdsFor(Book.class);
        disableHttpMethods(Book.class, config, persistingActions);

        /* Configure CORS Mapping */
        cors.addMapping(config.getBasePath() + "/**").allowedOrigins(REACT_APP);
    }

    private void disableHttpMethods(Class<Book> clazz, RepositoryRestConfiguration config, HttpMethod[] unsupportedActions) {

        config.getExposureConfiguration().forDomainType(clazz)
                .withItemExposure(((metdata, httpMethods) -> httpMethods.disable(unsupportedActions)))
                .withCollectionExposure(((metdata, httpMethods) -> httpMethods.disable(unsupportedActions)))
        ;
    }
}
