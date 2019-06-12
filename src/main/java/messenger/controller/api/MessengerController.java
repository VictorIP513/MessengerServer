package messenger.controller.api;

import messenger.controller.response.FriendStatus;
import messenger.controller.response.LoginResponse;
import messenger.controller.response.RegistrationResponse;
import messenger.controller.response.RestorePasswordResponse;
import messenger.model.PasswordStatus;
import messenger.model.User;
import messenger.model.UserDetails;
import messenger.properties.LocalizationProperties;
import messenger.service.AuthenticationTokenGenerator;
import messenger.service.FileStorageService;
import messenger.service.UserService;
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
                userService.notifyALoggedInOnNewDevice(user);
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

    @GetMapping("/api/confirmChangePasswordStatus/{passwordCode}")
    private ResponseEntity<String> confirmChangePasswordStatus(@PathVariable String passwordCode) {
        PasswordStatus passwordStatus = userService.getPasswordStatus(passwordCode);
        if (passwordStatus == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.confirmChangePassword(passwordStatus);
        return new ResponseEntity<>(
                LocalizationProperties.getProperty("change_password.change_successful"), HttpStatus.OK);
    }

    @GetMapping("/api/cancelChangePasswordStatus/{passwordCode}")
    private ResponseEntity<String> cancelChangePasswordStatus(@PathVariable String passwordCode) {
        PasswordStatus passwordStatus = userService.getPasswordStatus(passwordCode);
        if (passwordStatus == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.cancelChangePassword(passwordStatus);
        return new ResponseEntity<>(
                LocalizationProperties.getProperty("change_password.cancel"), HttpStatus.OK);
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
            if (userDetails == null || userDetails.getUserPhoto() == null) {
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

    @GetMapping("/api/getFriendStatus/{login}")
    private ResponseEntity<FriendStatus> getFriendStatus(
            @PathVariable String login, @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User friendUser = userService.getUserByLogin(login);
        if (friendUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        FriendStatus friendStatus = userService.getFriendStatus(user, friendUser);
        return new ResponseEntity<>(friendStatus, HttpStatus.OK);
    }

    @GetMapping("/api/getFriends")
    private ResponseEntity<List<User>> getFriends(
            @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<User> friends = userService.getFriends(user);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping("/api/getIncomingRequests")
    private ResponseEntity<List<User>> getIncomingRequests(
            @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<User> friends = userService.getIncomingRequests(user);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping("/api/getOutgoingRequests")
    private ResponseEntity<List<User>> getOutgoingRequests(
            @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<User> friends = userService.getOutgoingRequests(user);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping("/api/getBlockStatus/{login}")
    private ResponseEntity<Boolean> getBlockStatus(
            @PathVariable String login, @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User secondUser = userService.getUserByLogin(login);
        if (secondUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isBlockedUser = userService.isBlockedUser(user, secondUser);
        return new ResponseEntity<>(isBlockedUser, HttpStatus.OK);
    }

    @GetMapping("/api/getBlockYouStatus/{login}")
    private ResponseEntity<Boolean> getBlockYouStatus(
            @PathVariable String login, @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User secondUser = userService.getUserByLogin(login);
        if (secondUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean blockYouStatus = userService.getBlockYouStatus(user, secondUser);
        return new ResponseEntity<>(blockYouStatus, HttpStatus.OK);
    }

    @GetMapping("/api/getAllBlockedUsers")
    private ResponseEntity<List<User>> getBlockYouStatus(
            @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<User> blockedUsersList = userService.getBlockedUsersFromUser(user);
        return new ResponseEntity<>(blockedUsersList, HttpStatus.OK);
    }

    @PatchMapping("/api/addToFriend/{login}")
    private ResponseEntity<Void> addToFriend(
            @PathVariable String login, @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User friendUser = userService.getUserByLogin(login);
        if (friendUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.addToFriend(user, friendUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/api/deleteFromFriend/{login}")
    private ResponseEntity<Void> deleteFromFriend(
            @PathVariable String login, @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User friendUser = userService.getUserByLogin(login);
        if (friendUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.deleteFromFriend(user, friendUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/api/acceptFriendRequest/{login}")
    private ResponseEntity<Void> acceptFriendRequest(
            @PathVariable String login, @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User friendUser = userService.getUserByLogin(login);
        if (friendUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.acceptFriendRequest(user, friendUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/api/blockUser/{login}")
    private ResponseEntity<Void> blockUser(
            @PathVariable String login, @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User userToBlock = userService.getUserByLogin(login);
        if (userToBlock == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.blockUser(user, userToBlock);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/api/unlockUser/{login}")
    private ResponseEntity<Void> unlockUser(
            @PathVariable String login, @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User userToUnlock = userService.getUserByLogin(login);
        if (userToUnlock == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.unlockUser(user, userToUnlock);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/api/restorePassword")
    private ResponseEntity<RestorePasswordResponse> restorePassword(
            @RequestParam(name = "login") String login, @RequestParam(name = "newPassword") String newPassword) {
        User user = userService.getUserByLogin(login);
        if (user == null) {
            return new ResponseEntity<>(RestorePasswordResponse.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        userService.changePassword(user, newPassword);
        return new ResponseEntity<>(RestorePasswordResponse.CONFIRMATION_EMAIL_SENT, HttpStatus.OK);
    }

    @PatchMapping("/api/userIsOnline")
    private ResponseEntity<Void> userIsOnline(@RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        userService.setUserIsOnline(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
