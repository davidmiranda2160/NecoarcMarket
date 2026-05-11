package cl.duoc.usuario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "usuario")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false, length= 100)
    private String nombre;

    @Column(nullable= false, length= 150)
    private String apellidos;

    @Column(nullable= false, length= 255)
    private String correo;

    @Column(nullable= false, length= 30)
    private String direccion;

    @Column(nullable= false, length= 50)
    private String telefono;

    @Column(nullable= false, length=30)
    private String tipoUsuario;

}
