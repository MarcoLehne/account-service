package account.repository;

import account.entity.SecurityEvent;
import org.springframework.data.repository.CrudRepository;

public interface SecurityRepository extends CrudRepository<SecurityEvent, Integer> {
}
