package me.integrate.socialbank.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/users/{email}/password")
    public void updatePassword(@PathVariable String email, @RequestBody String password) {
        userService.updatePassword(email, password);
    }

    @PutMapping("/users/{email}/update")
    public void updateUser(@PathVariable String email, @RequestBody User user) {
        userService.updateUser(email, user);
    }
}
