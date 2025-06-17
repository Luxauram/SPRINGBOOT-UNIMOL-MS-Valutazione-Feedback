package it.unimol.microservice_user_role.dto.converter;

import it.unimol.microservice_user_role.dto.role.RoleDTO;
import it.unimol.microservice_user_role.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleConverter {

    /**
     * Converte un'entità Role in un RoleDTO
     *
     * @param role L'entità Role da convertire
     * @return Il RoleDTO corrispondente
     */
    public RoleDTO toDto(Role role) {
        if (role == null) {
            return null;
        }

        return new RoleDTO(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }

    /**
     * Converte un RoleDTO in un'entità Role
     *
     * @param roleDTO Il RoleDTO da convertire
     * @return L'entità Role corrispondente
     */
    public Role toEntity(RoleDTO roleDTO) {
        if (roleDTO == null) {
            return null;
        }

        return new Role(
                roleDTO.id(),
                roleDTO.name(),
                roleDTO.description()
        );
    }
}
