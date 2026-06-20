package cl.duoc.usuario.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import cl.duoc.usuario.model.Usuario;
import cl.duoc.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@Profile("Dev")
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    Faker faker = new Faker();

    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < 20; i++) {
            Usuario usuario = new Usuario();

            // Asignamos los datos simulados al objeto (en minúscula)
            usuario.setNombre(faker.name().firstName());
            usuario.setAppaterno(faker.name().lastName());
            usuario.setApmaterno(faker.name().lastName());
            usuario.setCorreo(faker.internet().emailAddress());
            usuario.setDireccion("Avenida Siempre Viva " + faker.number().numberBetween(100, 9999));
            usuario.setTelefono("+569" + faker.number().digits(8));
            usuario.setTipoUsuario("Cliente");
            usuarioRepository.save(usuario);
        }

    }

}
