package account.bootstrap;

import account.entity.Role;
import account.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RolesLoader implements CommandLineRunner {

    private final RolesRepository rolesRepository;

    @Autowired
    public RolesLoader(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
        createRoles();
    }

    private void createRoles() {

        createRoleIfNotExists("ROLE_ADMINISTRATOR");
        createRoleIfNotExists("ROLE_USER");
        createRoleIfNotExists("ROLE_ACCOUNTANT");
        createRoleIfNotExists("ROLE_AUDITOR");
    }

    private void createRoleIfNotExists(String roleName) {
        if (rolesRepository.findByRole(roleName).isEmpty()) {
            rolesRepository.save(new Role(roleName));
        }
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
