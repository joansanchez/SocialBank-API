package me.integrate.socialbank.event;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User does not have enough hours.")
public class UserNotEnoughHoursException extends RuntimeException {
}
