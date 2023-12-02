package account.entity;

import account.DTO.PaymentTransaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Entity
public class SalaryPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "appUser_id")
    private AppUser appUser;

    //@Pattern(regexp = "^(0[1-9]|[10-12])-(19[0-9]{2}|200[0-9]|201[0-9]|202[0-3])$")
    private String period;

//    @Min(0)
    private Long salary;

    public SalaryPayment() { }

    public SalaryPayment(String period, Long salary, AppUser appUser) {
        this.period = period;
        this.salary = salary;
        this.appUser = appUser;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
}
