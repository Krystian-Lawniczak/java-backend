package com.praca.thymeleaf.backend.services;

import com.praca.thymeleaf.backend.models.ChangePasswordRequest;
import com.praca.thymeleaf.backend.models.User;
import com.praca.thymeleaf.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ✅ Poprawione - używamy PasswordEncoder (interfejs)
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, // ✅ Wstrzykujemy PasswordEncoder zamiast BCryptPasswordEncoder
                       @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public void testPassword() {
        String rawPassword = "chad123456"; // Surowe hasło
        String hashedPasswordFromDB = "$2a$10$XTFB8K.EXVN96WhnEXV/5eg0UsoSgy1DK4VhMVG.2TC8WCJK7YrNq"; // Z bazy

        boolean matches = passwordEncoder.matches(rawPassword, hashedPasswordFromDB);
        System.out.println("🔍 Test ręcznego porównania: " + matches);
    }

    @PostConstruct
    public void runPasswordTest() {
        testPassword();
    }

    public void testEncoder() {
        String rawPassword = "chad123456";

        // 🔥 Utwórz NOWY obiekt BCryptPasswordEncoder() do testów
        BCryptPasswordEncoder testEncoder = new BCryptPasswordEncoder();

        String testHash = testEncoder.encode(rawPassword); // Nowy hash
        System.out.println("🆕 Testowy nowy hash: " + testHash);

        boolean testMatches = testEncoder.matches(rawPassword, testHash);
        System.out.println("✅ Test wewnętrzny encodera: " + testMatches);
    }

    @PostConstruct
    public void runEncoderTest() {
        testEncoder();
    }

    public void testEncoderConsistency() {
        String rawPassword = "chad123456";

        // 🔥 Sprawdzenie, czy `passwordEncoder` wstrzyknięty do UserService działa poprawnie
        String hashFromUserService = passwordEncoder.encode(rawPassword);
        System.out.println("🔑 Hash z UserService: " + hashFromUserService);

        // 🔥 Sprawdzenie, czy nowa instancja BCryptPasswordEncoder daje ten sam efekt
        BCryptPasswordEncoder bcryptTestEncoder = new BCryptPasswordEncoder();
        String hashFromNewInstance = bcryptTestEncoder.encode(rawPassword);
        System.out.println("🆕 Hash z nowego BCryptPasswordEncoder: " + hashFromNewInstance);

        // 🔥 Porównanie obu instancji
        boolean matches = bcryptTestEncoder.matches(rawPassword, hashFromUserService);
        System.out.println("✅ Czy encodery są zgodne?: " + matches);
    }

    @PostConstruct
    public void runEncoderConsistency() {
        testEncoderConsistency();
    }

    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }

        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new RuntimeException("Name already taken");
        }

        // 🔥 Debug: sprawdzamy, czy hasło jest już zaszyfrowane
        if (user.getPassword().startsWith("$2a$")) {
            throw new RuntimeException("❌ BŁĄD: Hasło jest już zaszyfrowane! Podwójne hashowanie!");
        }

        // 📝 Sprawdzenie, co przychodzi jako surowe hasło
        System.out.println("📝 Surowe hasło przed zapisaniem: " + user.getPassword());

        // ✅ Haszujemy hasło przed zapisaniem do bazy
        String hashedPassword = passwordEncoder.encode(user.getPassword());

        // 🔑 Sprawdzamy, jak wygląda zahashowane hasło
        System.out.println("🔑 Zahashowane hasło przed zapisaniem: " + hashedPassword);

        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }




    public String authenticateUser(String email, String password) {
        System.out.println("🔍 Szukam użytkownika o emailu: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        System.out.println("✅ Znaleziony użytkownik: " + user.getEmail());

        // 🔑 Pobranie hasła z bazy
        String hashedPasswordFromDB = user.getPassword();
        System.out.println("🔐 Hasło w bazie: " + hashedPasswordFromDB);

        // 🔑 Wyświetlenie podanego hasła przed porównaniem
        System.out.println("📝 Surowe podane hasło: " + password);

        // 🔥 Porównujemy czyste hasło z hasłem w bazie
        boolean matches = passwordEncoder.matches(password, hashedPasswordFromDB);
        System.out.println("✅ Czy hasło pasuje?: " + matches);


        if (!matches) {
            throw new RuntimeException("❌ Błędne hasło!");
        }

        return user.getEmail();
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik o nazwie " + username + " nie istnieje"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }
}
