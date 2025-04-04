package MyChillZone.web;

import MyChillZone.exception.UsernameAlreadyExistException;
import MyChillZone.security.AuthenticationMetadata;
import MyChillZone.user.model.User;
import MyChillZone.user.model.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

@ControllerAdvice
public class ExceptionAdvice {
    private final UserService userService;

    @Autowired
    public ExceptionAdvice(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String handleUserAlreadyExist(RedirectAttributes redirectAttributes, HttpServletRequest request){

        String username= request.getParameter("username");
        String message = "%s is already in use!".formatted(username);

        redirectAttributes.addFlashAttribute("usernameAlreadyExistMessage", message);
        return "redirect:/register";
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class, // Когато се опитва да достъпи ендпойнт, до който не му е позволено/нямам достъп
            NoResourceFoundException.class,  // Когато се опитва да достъпи невалиден ендпойнт
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class
    })
    public ModelAndView handleNotFoundExceptions(Exception exception, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){

        return new ModelAndView("not-found");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyExceptions(Exception e){

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("internal-server-error");
        modelAndView.addObject("errorMessage", e.getClass().getSimpleName());

        return modelAndView;
    }

}
