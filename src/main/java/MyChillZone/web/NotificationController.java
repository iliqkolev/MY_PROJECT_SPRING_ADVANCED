package MyChillZone.web;

import MyChillZone.notification.client.dto.Notification;
import MyChillZone.notification.client.dto.NotificationPreference;
import MyChillZone.notification.client.service.NotificationService;
import MyChillZone.security.AuthenticationMetadata;
import MyChillZone.user.model.User;
import MyChillZone.user.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ModelAndView getNotificationPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){

        User user = userService.getById(authenticationMetadata.getUserId());

        NotificationPreference notificationPreference = notificationService.getNotificationPreference(user.getId());
        List<Notification> notificationHistory = notificationService.getNotificationHistory(user.getId());
        long succeededNotificationsNumber = notificationHistory.stream().filter(n -> n.getStatus().equals("SUCCEEDED")).count();
        long failedNotificationsNumber = notificationHistory.stream().filter(n -> n.getStatus().equals("FAILED")).count();
        notificationHistory = notificationHistory.stream().limit(5).toList();


        ModelAndView modelAndView = new ModelAndView("notification");
        modelAndView.addObject("user", user);
        modelAndView.addObject("notificationPreference", notificationPreference);
        modelAndView.addObject("succeededNotificationsNumber", succeededNotificationsNumber);
        modelAndView.addObject("failedNotificationsNumber", failedNotificationsNumber);
        modelAndView.addObject("notificationHistory", notificationHistory);

        return modelAndView;
    }

    @PutMapping("/user-preference")
    public String updateUserPreference(@RequestParam(name = "enabled") boolean enabled, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){

        notificationService.updateNotificationPreference(authenticationMetadata.getUserId(), enabled);

        return "redirect:/notifications";
    }




}
