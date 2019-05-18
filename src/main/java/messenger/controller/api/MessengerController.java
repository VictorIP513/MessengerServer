package messenger.controller.api;

import messenger.controller.response.LoginResponse;
import messenger.controller.response.RegistrationResponse;
import messenger.model.User;
import messenger.model.UserDetails;
import messenger.service.AuthenticationTokenGenerator;
import messenger.service.FileStorageService;
import messenger.service.UserService;
import messenger.view.LocalizationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@SuppressWarnings("unused")
@Controller
public class MessengerController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/api/registration")
    private ResponseEntity<RegistrationResponse> registration(@RequestBody User user) {
        if (userService.isExistLogin(user.getLogin())) {
            return new ResponseEntity<>(RegistrationResponse.LOGIN_IS_EXISTS, HttpStatus.CONFLICT);
        }
        if (userService.isExistEmail(user.getEmail())) {
            return new ResponseEntity<>(RegistrationResponse.EMAIL_IS_EXISTS, HttpStatus.CONFLICT);
        }

        userService.registerNewUser(user);
        return new ResponseEntity<>(RegistrationResponse.REGISTRATION_SUCCESSFUL, HttpStatus.OK);
    }

    @PostMapping("/api/login")
    private ResponseEntity<LoginResponse> login(@RequestParam(name = "login") String login,
                                                @RequestParam(name = "password") String password) {
        User user = userService.getUserByLoginAndPassword(login, password);
        if (user != null) {
            if (userService.isActivateUser(user)) {
                String authenticationToken = AuthenticationTokenGenerator.getToken();
                userService.setNewAuthenticationTokenToUser(authenticationToken, user);
                LoginResponse response = new LoginResponse(LoginResponse.Status.LOGIN_SUCCESSFUL, authenticationToken);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                LoginResponse response =
                        new LoginResponse(LoginResponse.Status.ACCOUNT_NOT_CONFIRMED, null);
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        }
        LoginResponse response =
                new LoginResponse(LoginResponse.Status.INVALID_LOGIN_OR_PASSWORD, null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/api/uploadPhoto")
    private ResponseEntity<Void> uploadPhoto(@ModelAttribute(name = "photo") MultipartFile photo,
                                             @RequestParam(name = "authenticationToken") String authenticationToken) {
        if (!userService.checkCorrectAuthenticationToken(authenticationToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        String pathToPhoto = fileStorageService.storeFile(photo, user);
        userService.uploadUserPhoto(pathToPhoto, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/activate/{activationCode}")
    private ResponseEntity<String> activate(@PathVariable String activationCode) {
        boolean isActivated = userService.activateUser(activationCode);
        if (isActivated) {
            return new ResponseEntity<>(
                    LocalizationProperties.getProperty("activation.activation_successful"), HttpStatus.OK);
        }
        return new ResponseEntity<>(
                LocalizationProperties.getProperty("activation.code_is_invalid"), HttpStatus.OK);
    }

    @GetMapping("/api/checkAuthenticationToken/{authenticationToken}")
    private ResponseEntity<Boolean> checkCorrectAuthenticationToken(@PathVariable String authenticationToken) {
        boolean isCorrectAuthenticationToken = userService.checkCorrectAuthenticationToken(authenticationToken);
        return new ResponseEntity<>(isCorrectAuthenticationToken, HttpStatus.OK);
    }

    @GetMapping("/api/getUser/{login}")
    private ResponseEntity<User> getUser(@PathVariable String login) {
        User user = userService.getUserByLogin(login);
        if (user != null) {
            user.setPassword(null);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/api/getUserPhoto/{login}")
    private ResponseEntity<Resource> getUserPhoto(@PathVariable String login) {
        User user = userService.getUserByLogin(login);
        if (user != null) {
            UserDetails userDetails = userService.getUserDetailsByUser(user);
            if (userDetails == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            String path = fileStorageService.getFullPathToFile(userDetails.getUserPhoto());
            Resource resource = fileStorageService.getResourceFromFile(path);
            if (resource != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG_VALUE))
                        .body(resource);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/api/getAllUsers")
    private ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
