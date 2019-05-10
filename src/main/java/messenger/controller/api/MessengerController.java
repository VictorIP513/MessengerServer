package messenger.controller.api;

import messenger.controller.response.RegistrationResponse;
import messenger.model.User;
import messenger.service.UserService;
import messenger.view.LocalizationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
}
