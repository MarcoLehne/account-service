package account.service;

import account.entity.AppUser;
import account.entity.Role;
import account.repository.RolesRepository;
import account.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolesService {

    UserRepository userRepository;
    RolesRepository rolesRepository;

    public RolesService(UserRepository userRepository, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
    }

    @Transactional
    public void assignRole(AppUser appUser, Role role) {
        appUser.addRole(role);
        role.addUser(appUser);
    }

    @Transactional
    public void removeRole(AppUser appUser, Role role) {
        appUser.removeRole(role);
        role.removeUser(appUser);
    }

    public List<String> getRolesAsString() {
        List<String> rolesAsString = new ArrayList<>();

        for (Role role: rolesRepository.findAll()) {
            rolesAsString.add(role.getRole());
        }
        return rolesAsString;
    }
}
