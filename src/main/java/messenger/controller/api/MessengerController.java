package messenger.controller.api;

import messenger.controller.response.LoginResponse;
import messenger.controller.response.RegistrationResponse;
import messenger.model.User;
import messenger.service.AuthenticationTokenGenerator;
import messenger.service.UserService;
import messenger.utils.StringUtils;
import messenger.view.LocalizationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@Controller
public class MessengerController {

    @Autowired
    private UserService userService;

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
        login = StringUtils.removeBeginAndEndQuotes(login);
        password = StringUtils.removeBeginAndEndQuotes(password);
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
}
