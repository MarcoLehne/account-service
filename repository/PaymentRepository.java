package account.repository;

import account.DTO.PaymentTransaction;
import account.entity.AppUser;
import account.entity.SalaryPayment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<SalaryPayment, Integer> {
    List<SalaryPayment> findByAppUser(AppUser appUser);
    Optional<SalaryPayment> findByPeriodAndAppUser(String period, AppUser appUser);
}
