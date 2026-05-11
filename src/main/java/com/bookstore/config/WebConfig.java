package com.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Web MVC configuration:
 * - Serve static frontend files from /static
 * - Enable CORS for local frontend development (e.g. Live Server on port 5500)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Allow the Spring-served frontend AND a separate dev server (e.g. VS Code Live Server)
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080",
                                "http://localhost:5500", "http://127.0.0.1:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve everything in src/main/resources/static/ at the root URL
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // SPA fallback: serve index.html for any path that doesn't match a REST endpoint
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
