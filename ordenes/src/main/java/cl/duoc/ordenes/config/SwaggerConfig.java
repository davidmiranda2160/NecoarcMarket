package cl.duoc.ordenes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;


/*
Esta clase sirve para personalizar la informacion que aparece en la
documentacion de la API.
*/

@Configuration //Le indica a spring que contiene configuraciones y beans.
public class SwaggerConfig {

    /* 
    Le dice a spring que debe crear un objeto y guardarlo en un contenedor de spring
    y SpringDoc detectara este bean de tipo OpenAPI para construir la documentacion.
    */
    @Bean 
    public OpenAPI customOpenAPI(){
        return new OpenAPI() //Se crea la instancia de OpenAPI
                .info(new Info() //Se define la informacio de la documentacion
                    .title("API para gestionar ordenes del sistema Necoarcmarkert")
                    .version("2.0")
                    .description("Documentacion de la API para el sistema Necoarcmarket"));
    }
}
