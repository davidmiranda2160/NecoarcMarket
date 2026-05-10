package cl.duoc.resena.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResenaResponse {

    private Long id;
    private Long productoId;
    private Long usuarioId;
    private String nombreUsuario; 
    private int calificacion;
    private String comentario;
    private String fechaCreacion;
}