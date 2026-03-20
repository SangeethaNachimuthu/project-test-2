package se.lexicon.subscriptionapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.constant.Role;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;
import se.lexicon.subscriptionapi.domain.entity.Customer;
import se.lexicon.subscriptionapi.domain.entity.Operator;
import se.lexicon.subscriptionapi.domain.entity.Plan;
import se.lexicon.subscriptionapi.repository.CustomerRepository;
import se.lexicon.subscriptionapi.repository.OperatorRepository;
import se.lexicon.subscriptionapi.repository.PlanRepository;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final OperatorRepository operatorRepository;
    private final PlanRepository planRepository;

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
        seedRegularUser();
        seedOperatorAndPlan();
    }

    private void seedAdminUser() {
        String adminEmail = "admin@example.com";
        if (!customerRepository.existsByEmail(adminEmail)) {
            Customer admin = new Customer();
            admin.setEmail(adminEmail);
            admin.setFirstName("Admin");
            admin.setLastName("Adminson");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));
            customerRepository.save(admin);
            System.out.println("[DATA_SEED] Admin user created: " + adminEmail);
        }
    }

    private void seedRegularUser() {
        String userEmail = "user@example.com";
        String roseEmail = "rose@example.com";
        if (!customerRepository.existsByEmail(userEmail)) {
            Customer user = new Customer();
            user.setEmail(userEmail);
            user.setFirstName("User");
            user.setLastName("Userson");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRoles(Set.of(Role.ROLE_USER));
            customerRepository.save(user);
            System.out.println("[DATA_SEED] Regular user created: " + userEmail);
        }
        if (!customerRepository.existsByEmail(roseEmail)) {
            Customer rose = new Customer();
            rose.setEmail(roseEmail);
            rose.setFirstName("Rose");
            rose.setLastName("Mari");
            rose.setPassword(passwordEncoder.encode("password"));
            rose.setRoles(Set.of(Role.ROLE_USER));
            customerRepository.save(rose);
            System.out.println("[DATA_SEED] Regular user created: " + roseEmail);
        }
    }

    private void seedOperatorAndPlan() {
        Operator fiberNet = new Operator();
        Operator teliaNet = new Operator();
        fiberNet.setName("Fiber Net");
        teliaNet.setName("telia Net");
        operatorRepository.save(fiberNet);
        operatorRepository.save(teliaNet);
        System.out.println("[DATA_SEED] Operator created: " + fiberNet);

        Plan fiber10 = new Plan();
        fiber10.setName("Fiber 10");
        fiber10.setPrice(BigDecimal.valueOf(50.00));
        fiber10.setOperator(fiberNet);
        fiber10.setServiceType(ServiceType.INTERNET);
        fiber10.setStatus(PlanStatus.ACTIVE);
        planRepository.save(fiber10);

        Plan fiber50 = new Plan();
        fiber50.setName("Fiber 50");
        fiber50.setPrice(BigDecimal.valueOf(150.00));
        fiber50.setOperator(fiberNet);
        fiber50.setServiceType(ServiceType.INTERNET);
        fiber50.setStatus(PlanStatus.ACTIVE);
        planRepository.save(fiber50);

        Plan fiberMobPlus = new Plan();
        fiberMobPlus.setName("Fiber Mobile Plus");
        fiberMobPlus.setPrice(BigDecimal.valueOf(200.00));
        fiberMobPlus.setOperator(fiberNet);
        fiberMobPlus.setServiceType(ServiceType.MOBILE);
        fiberMobPlus.setDataLimit("50");
        fiberMobPlus.setStatus(PlanStatus.ACTIVE);
        planRepository.save(fiberMobPlus);

        Plan telia10 = new Plan();
        telia10.setName("Telia 10");
        telia10.setPrice(BigDecimal.valueOf(50.00));
        telia10.setOperator(teliaNet);
        telia10.setServiceType(ServiceType.INTERNET);
        telia10.setStatus(PlanStatus.ACTIVE);
        planRepository.save(telia10);

        Plan mobileBasic= new Plan();
        mobileBasic.setName("Telia Mobile Basic");
        mobileBasic.setPrice(BigDecimal.valueOf(40.00));
        mobileBasic.setOperator(teliaNet);
        mobileBasic.setServiceType(ServiceType.MOBILE);
        mobileBasic.setDataLimit("10");
        mobileBasic.setStatus(PlanStatus.ACTIVE);
        planRepository.save(mobileBasic);
    }



}
