package cl.duoc.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResenaRequest {

    @Schema(description = "id del producto", example = "8")
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @Schema(description = "id del usuario", example = "1")
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @Schema(description = "calificacion del producto entre 1 y 5", example = "5")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private int calificacion;

    @Schema(description = "comentario reseña", example = "me encanta avatar belial")
    @NotBlank(message = "El comentario no puede estar vacío")
    private String comentario;
}
