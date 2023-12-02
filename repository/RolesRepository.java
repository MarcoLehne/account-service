package account.repository;

import account.entity.AppUser;
import account.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByRole(String role);

}
