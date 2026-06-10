package com.mascotas.mascotas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    
    @org.springframework.context.annotation.Bean
    public org.springframework.web.client.RestTemplate restTemplate() {
        return new org.springframework.web.client.RestTemplate();
    }
    
     @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Rutas para leer las fotos de los reportes
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        
        // Rutas de emergencia para que cargue la interfaz gráfica
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .map(c -> (MappingJackson2HttpMessageConverter) c)
                .findFirst()
                .ifPresent(converter -> {
                    // Tomamos la lista de formatos que Spring ya acepta (como JSON)
                    List<MediaType> mediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
                    // Le agregamos a la fuerza el formato "tonto" que envía Swagger
                    mediaTypes.add(new MediaType("application", "octet-stream"));
                    converter.setSupportedMediaTypes(mediaTypes);
                });
    }
}
