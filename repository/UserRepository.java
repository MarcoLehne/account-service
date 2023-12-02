package account.repository;

import account.entity.AppUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Integer> {
        Optional<AppUser> findUserByUsername(String username);
        Optional<AppUser> findByEmail(String email);
        Optional<AppUser> findByUsername(String email);
        @Query("SELECT u FROM AppUser u JOIN u.roles r WHERE r.role = :roleName")
        Optional<AppUser> findUserByRoleName(String roleName);
}
