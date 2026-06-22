package cl.duoc.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResenaResponse {

    @Schema(description = "id automatica de la reseña", example = "1")
    private Long id;

    @Schema(description = "id del producto", example = "1")
    private Long productoId;

    @Schema(description = "id del usuario de la reseña", example = "1")
    private Long usuarioId;

    @Schema(description = "nombre del usuario que hizo la reseña", example = "carlos")
    private String nombreUsuario; 

    @Schema(description = "calificacion de la persona que hizo la reseña", example = "4")
    private int calificacion;

    @Schema(description = "comentario de la reseña", example = "dulce de leche che, +10 y favoritos")
    private String comentario;

    @Schema(description = "fecha de la reseña", example = "2026-05-10 22:00:00")
    private String fechaCreacion;
}