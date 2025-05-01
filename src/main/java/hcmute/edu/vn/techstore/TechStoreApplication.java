package hcmute.edu.vn.techstore;

import hcmute.edu.vn.techstore.Enum.EPayment;
import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.Enum.EGender;
import hcmute.edu.vn.techstore.entity.*;
import hcmute.edu.vn.techstore.repository.*;
import hcmute.edu.vn.techstore.service.interfaces.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.math.BigDecimal;

@SpringBootApplication
@RequiredArgsConstructor
public class TechStoreApplication {

	private final RoleRepository roleRepository;
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PaymentRepository paymentRepository;

	public static void main(String[] args) {
		SpringApplication.run(TechStoreApplication.class, args);
	}

	@Bean
	CommandLineRunner init() {
		return args -> {
			// Create roles if they don't exist
			RoleEntity adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
				.orElseGet(() -> {
					RoleEntity role = new RoleEntity();
					role.setName(ERole.ROLE_ADMIN);
					return roleRepository.save(role);
				});

			RoleEntity staffRole = roleRepository.findByName(ERole.ROLE_STAFF)
				.orElseGet(() -> {
					RoleEntity role = new RoleEntity();
					role.setName(ERole.ROLE_STAFF);
					return roleRepository.save(role);
				});

			RoleEntity customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
					.orElseGet(() -> {
						RoleEntity role = new RoleEntity();
						role.setName(ERole.ROLE_CUSTOMER);
						return roleRepository.save(role);
					});

			// Create admin account if it doesn't exist
			if (accountRepository.findByEmail("admin@techstore.com").isEmpty()) {
				// Create cart for admin
				CartEntity adminCart = new CartEntity();
				adminCart.setTotalPrice(BigDecimal.ZERO);

				// Create wallet for admin
				WalletEntity adminWallet = new WalletEntity();
				adminWallet.setBalance(BigDecimal.ZERO);

				// Create admin account
				AccountEntity adminAccount = new AccountEntity();
				adminAccount.setEmail("admin@techstore.com");
				adminAccount.setPassword(passwordEncoder.encode("admin123"));

				// Create admin user
				UserEntity adminUser = new UserEntity();
				adminUser.setFirstName("Admin");
				adminUser.setLastName("User");
				adminUser.setPhoneNumber("0123456789");
				adminUser.setGender(EGender.MALE);
				adminUser.setImage("default.png");
				adminUser.setRole(adminRole);
				adminUser.setCart(adminCart);
				adminUser.setActived(true);
				adminUser.setWallet(adminWallet);
				adminUser.setAccount(adminAccount);
				adminAccount.setUser(adminUser);

				// Save the user (which will cascade to account, cart, and wallet)
				userRepository.save(adminUser);
			}

			// Create staff account if it doesn't exist
			if (accountRepository.findByEmail("staff@techstore.com").isEmpty()) {
				// Create cart for staff
				CartEntity staffCart = new CartEntity();
				staffCart.setTotalPrice(BigDecimal.ZERO);

				// Create wallet for staff
				WalletEntity staffWallet = new WalletEntity();
				staffWallet.setBalance(BigDecimal.ZERO);

				// Create staff account
				AccountEntity staffAccount = new AccountEntity();
				staffAccount.setEmail("staff@techstore.com");
				staffAccount.setPassword(passwordEncoder.encode("staff123"));

				// Create staff user
				UserEntity staffUser = new UserEntity();
				staffUser.setFirstName("Staff");
				staffUser.setLastName("User");
				staffUser.setPhoneNumber("0987654321");
				staffUser.setGender(EGender.MALE);
				staffUser.setImage("default.png");
				staffUser.setActived(true);
				staffUser.setRole(staffRole);
				staffUser.setCart(staffCart);
				staffUser.setWallet(staffWallet);
				staffUser.setAccount(staffAccount);
				staffAccount.setUser(staffUser);

				// Save the user (which will cascade to account, cart, and wallet)
				userRepository.save(staffUser);
			}

			// Create admin account if it doesn't exist
			if (accountRepository.findByEmail("customer@techstore.com").isEmpty()) {
				// Create cart for admin
				CartEntity customerCart = new CartEntity();
				customerCart.setTotalPrice(BigDecimal.ZERO);

				// Create wallet for admin
				WalletEntity customerWallet = new WalletEntity();
				customerWallet.setBalance(BigDecimal.ZERO);

				// Create admin account
				AccountEntity customerAccount = new AccountEntity();
				customerAccount.setEmail("customer@techstore.com");
				customerAccount.setPassword(passwordEncoder.encode("customer123"));

				// Create admin user
				UserEntity customerUser = new UserEntity();
				customerUser.setFirstName("Customer");
				customerUser.setLastName("User");
				customerUser.setPhoneNumber("0363257425");
				customerUser.setGender(EGender.FEMALE);
				customerUser.setImage("default.png");
				customerUser.setRole(customerRole);
				customerUser.setCart(customerCart);
				customerUser.setActived(true);
				customerUser.setWallet(customerWallet);
				customerUser.setAccount(customerAccount);
				customerAccount.setUser(customerUser);

				// Save the user (which will cascade to account, cart, and wallet)
				userRepository.save(customerUser);
			}

			// Create payment methods if they don't exist
			for (EPayment payment : EPayment.values()) {
				if (paymentRepository.findByName(payment.name()).isEmpty()) {
					PaymentEntity paymentEntity = new PaymentEntity();
					paymentEntity.setName(payment.name());
					paymentRepository.save(paymentEntity);
				}
			}
		};
	}
}
