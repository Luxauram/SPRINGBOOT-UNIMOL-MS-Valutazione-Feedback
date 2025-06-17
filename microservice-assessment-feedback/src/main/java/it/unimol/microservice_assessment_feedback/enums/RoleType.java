package it.unimol.microservice_assessment_feedback.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo di Utente esistente (e.g., STUDENT, TEACHER, ADMIN, SUPER_ADMIN)")
public enum RoleType {
    STUDENT("STUDENT", "STUDENT", 0),
    TEACHER("TEACHER", "TEACHER", 1),
    ADMIN("ADMIN", "ADMIN", 2),
    SUPER_ADMIN("SUPER_ADMIN", "SUPER_ADMIN", 3);

    private final String roleId;
    private final String roleName;
    private final int level;

    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

    RoleType(String roleId, String roleName, int level) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.level = level;
    }

    public String getRoleId() { return roleId; }
    public String getRoleName() { return roleName; }
    public int getLevel() { return level; }

    public static RoleType fromRoleId(String roleId) {
        for (RoleType role : RoleType.values()) {
            if (role.roleId.equalsIgnoreCase(roleId)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Ruolo con ID '" + roleId + "' non trovato.");
    }

    public static RoleType fromRoleName(String roleName) {
        for (RoleType role : RoleType.values()) {
            if (role.roleName.equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Ruolo con nome '" + roleName + "' non trovato.");
    }

    public boolean hasMinimumLevel(RoleType requiredRole) {
        return this.level >= requiredRole.level;
    }
}