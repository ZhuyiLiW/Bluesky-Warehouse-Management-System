package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Repository.UserRepository;
import com.example.blueskywarehouse.Entity.LoginUserDetails;
import com.example.blueskywarehouse.Entity.User;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Exception.InvalidParameterException;
import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Util.SqlLikeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(OptimalStorageLocationService.class);

    /**
     * Gibt die Benutzer-ID für einen gegebenen Benutzernamen zurück.
     * Überprüft, ob der Benutzername gültig ist und ob der Benutzer existiert.
     *
     * @param userName Der Benutzername
     * @return ApiResponse mit Benutzer-ID und überprüftem Benutzernamen
     */
    public ApiResponse<?> getUserId(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            logger.warn("Benutzer-ID konnte nicht abgerufen werden: Benutzername ist leer");
            throw new InvalidParameterException("Benutzername darf nicht leer sein");
        }

        // Benutzer-ID abrufen
        Integer userId = userRepository.userId(SqlLikeEscaper.escape(userName));
        String checkUserName = userRepository.checkUserName(userId);

        // Prüfen, ob Benutzer existiert
        if (userId == null) {
            logger.info("Benutzername nicht gefunden: {}", userName);
            throw new BusinessException("Benutzer nicht gefunden");
        }

        logger.info("Benutzer-ID erfolgreich abgerufen: userName={}, userId={}", userName, userId);
        return ApiResponse.success("Benutzer-ID erfolgreich abgerufen", userId + ":" + checkUserName);
    }

    /**
     * Fügt einen neuen Benutzer dem System hinzu.
     * Führt Validierungen durch (leere Eingaben, Passwortlänge, vorhandener Benutzername).
     */
    @Transactional
    public ApiResponse<?> addNewUser(String userName, String password, int role) {

        logger.info("Versuche, neuen Benutzer hinzuzufügen, Benutzername: {}", userName);

        if (userName == null || password == null || userName.trim().isEmpty() || password.trim().isEmpty()) {
            logger.warn("Benutzer konnte nicht hinzugefügt werden: Benutzername oder Passwort ist leer");
            throw new InvalidParameterException("Benutzername oder Passwort darf nicht leer sein");
        }

        if (password.length() < 8) {
            logger.warn("Benutzer konnte nicht hinzugefügt werden: Passwort zu kurz, Benutzername: {}", userName);
            throw new BusinessException("Passwort muss länger als 8 Zeichen sein");
        }

        String isUserNameExisted = userRepository.getUserName(userName);
        if (isUserNameExisted != null) {
            logger.warn("Benutzer konnte nicht hinzugefügt werden: Benutzername existiert bereits, Benutzername: {}", userName);
            throw new BusinessException("Benutzername existiert bereits");
        }

        String encryptPwd = passwordEncoder.encode(password);
        User newUser = new User();
        newUser.setName(userName);
        newUser.setPwd(encryptPwd);
        newUser.setRoleId(role);
        userRepository.save(newUser);

        logger.info("Neuer Benutzer erfolgreich hinzugefügt: {}", userName);
        return ApiResponse.success("Benutzer erfolgreich hinzugefügt", null);
    }

    /**
     * Authentifiziert einen Benutzer mit Benutzername und Passwort.
     * Überprüft die Anmeldedaten und gibt Benutzerinformationen zurück.
     */
    public ApiResponse<?> login(String userName, String password) {

        logger.info("Benutzer versucht anzumelden, Benutzername: {}", userName);

        if (userName == null || userName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            logger.warn("Login fehlgeschlagen: Benutzername oder Passwort ist leer");
            throw new InvalidParameterException("Benutzername oder Passwort darf nicht leer sein");
        }

        String getUserName = userRepository.getUserName(userName);
        if (getUserName == null) {
            logger.warn("Login fehlgeschlagen: Benutzername [{}] existiert nicht", userName);
            throw new BusinessException("Benutzername existiert nicht");
        }

        String getPassword = userRepository.getPwd(userName);
        if (getPassword == null) {
            logger.warn("Login fehlgeschlagen: Passwort für Benutzername [{}] nicht gesetzt", userName);
            throw new BusinessException("Benutzerpasswort nicht gesetzt");
        }

        boolean matches = passwordEncoder.matches(password, getPassword);
        if (!matches) {
            logger.warn("Login fehlgeschlagen: Falsches Passwort für Benutzername [{}]", userName);
            throw new BusinessException("Falsches Passwort");
        }

        int getRoleId = userRepository.getRoleId(userName);
        logger.debug("Benutzername [{}] verifiziert, Rollen-ID: {}", userName, getRoleId);

        User thisUser = new User(userName, null, getRoleId);

        logger.info("Benutzer [{}] erfolgreich angemeldet", userName);
        return ApiResponse.success("Login erfolgreich", thisUser);
    }

    /**
     * Lädt die Benutzerdetails für Spring Security anhand des Benutzernamens.
     * Wird intern bei der Authentifizierung verwendet.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Versuche, Benutzer mit Namen [{}] zu laden", username);

        String userName = userRepository.getUserName(username); // Stelle sicher, dass diese Methode existiert
        Integer userRole = userRepository.getRoleId(username);
        logger.debug("Aus Datenbank geladene Rollen-ID: {}", userRole);

        User user = new User();
        user.setName(userName);
        user.setPwd(null);
        if (userRole != null) {
            user.setRoleId(userRole);
        }

        if (userName == null || userName.isEmpty()) {
            logger.warn("Benutzer [{}] existiert nicht", username);
            throw new UsernameNotFoundException("Benutzer existiert nicht");
        }

        logger.info("Benutzer [{}] erfolgreich geladen, bereite LoginUserDetails vor", userName);
        return new LoginUserDetails(user);
    }

    /**
     * Ändert die Rolle eines Benutzers anhand der Benutzer-ID.
     */
    @Transactional
    public ApiResponse<?> roleChange(int userId, int role) {
        User user = userRepository.findById((long) userId)
                .orElseThrow(() -> new RuntimeException("Benutzer existiert nicht " + userId));
        user.setRoleId(role);
        userRepository.save(user);

        logger.info("Benutzerrolle erfolgreich aktualisiert: Benutzer-ID={}, Rolle={}", userId, role);
        return ApiResponse.success("Aktualisierung erfolgreich", null);
    }

    /**
     * Gibt eine Liste aller Benutzer im System zurück.
     */
    public ApiResponse<?> getAllUser() {
        List<User> allUser = userRepository.findAll();
        logger.info("Benutzerliste erfolgreich abgerufen: {}", allUser);
        return ApiResponse.success("Alle Benutzer erfolgreich abgerufen", allUser);
    }

    /**
     * Löscht einen Benutzer aus dem System anhand der ID.
     */
    @Transactional
    public ApiResponse<?> deleteUser(int id) {
        userRepository.deleteById((long) id);
        logger.info("Benutzer erfolgreich gelöscht, ID: {}", id);
        return ApiResponse.success("Löschung erfolgreich", null);
    }
}
