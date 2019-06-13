package messenger.controller.api;

import messenger.model.Dialog;
import messenger.model.Message;
import messenger.model.User;
import messenger.service.DialogsService;
import messenger.service.FileStorageService;
import messenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@SuppressWarnings("unused")
@Controller
public class DialogsController {

    @Autowired
    private UserService userService;

    @Autowired
    private DialogsService dialogsService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/api/createDialog")
    private ResponseEntity<Dialog> createDialog(@RequestParam(name = "login") String login,
                                                @RequestParam(name = "lastMessageText") String lastMessageText,
                                                @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User userToSend = userService.getUserByLogin(login);
        if (userToSend == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Dialog dialog = dialogsService.createDialog(user, userToSend, lastMessageText);
        return new ResponseEntity<>(dialog, HttpStatus.OK);
    }

    @PostMapping("/api/sendMessage/{dialogId}")
    private ResponseEntity<Message> sendMessage(@PathVariable int dialogId,
                                                @RequestParam(name = "message") String message,
                                                @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Dialog dialog = dialogsService.getDialog(dialogId);
        if (dialog == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User secondUser = dialog.getUsers().iterator().next();
        boolean isBlocked = userService.isBlockedUser(secondUser, user);
        if (isBlocked) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Message responseMessage = dialogsService.sendMessage(message, dialog, user);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PostMapping("/api/sendImage/{dialogId}")
    private ResponseEntity<Message> sendImage(@PathVariable int dialogId,
                                              @ModelAttribute(name = "image") MultipartFile image,
                                              @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Dialog dialog = dialogsService.getDialog(dialogId);
        if (dialog == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User secondUser = dialog.getUsers().iterator().next();
        boolean isBlocked = userService.isBlockedUser(secondUser, user);
        if (isBlocked) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String pathToFile = fileStorageService.storeFileFromDialog(dialogId, image);
        Message responseMessage = dialogsService.sendImage(pathToFile, dialog, user);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @GetMapping("/api/getDialog/{login}")
    private ResponseEntity<Dialog> getDialog(
            @PathVariable String login, @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User secondUser = userService.getUserByLogin(login);
        if (secondUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Dialog dialog = dialogsService.getDialog(user, secondUser);
        if (dialog == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dialog, HttpStatus.OK);
    }

    @GetMapping("/api/getAllDialogs")
    private ResponseEntity<List<Dialog>> getAllDialogs(
            @RequestParam(name = "authenticationToken") String authenticationToken) {
        User user = userService.getUserByAuthenticationToken(authenticationToken);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Dialog> dialogs = dialogsService.getAllDialogs(user);
        if (dialogs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dialogs, HttpStatus.OK);
    }
}
