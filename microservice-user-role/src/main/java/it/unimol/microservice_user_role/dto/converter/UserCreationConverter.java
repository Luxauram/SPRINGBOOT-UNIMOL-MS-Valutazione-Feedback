package it.unimol.microservice_user_role.dto.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import java.util.Random;

import it.unimol.microservice_user_role.dto.user.CreateUserDTO;
import it.unimol.microservice_user_role.dto.role.RoleDTO;
import it.unimol.microservice_user_role.model.User;
import it.unimol.microservice_user_role.repository.UserRepository;
import it.unimol.microservice_user_role.service.RoleService;

@Component
public class UserCreationConverter implements Converter<CreateUserDTO, User> {
    @Autowired
    private RoleConverter roleConverter;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRepository userRepository;

    private static final Random random = new Random();

    @Override
    public User convert(@NonNull CreateUserDTO source) {

        RoleDTO ruolo = roleService.findById(source.roleId());
        String randomId;
        do {
            randomId = String.valueOf(100000 + random.nextInt(900000));
        } while (userRepository.findById(randomId).isPresent());

        return new User(randomId, source.username(), source.email(), source.name(), source.surname(),
                source.password(), roleConverter.toEntity(ruolo));
    }
}
