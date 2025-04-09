//package MyChillZone.web;
//
//import MyChillZone.security.AuthenticationMetadata;
//import MyChillZone.subscription.model.SubscriptionType;
//import MyChillZone.subscription.model.service.SubscriptionService;
//import MyChillZone.transaction.model.Transaction;
//import MyChillZone.user.model.User;
//import MyChillZone.user.model.service.UserService;
//import MyChillZone.web.dto.UpgradeRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.ModelAndView;
//
//@Controller
//@RequestMapping("/subscriptions")
//public class SubscriptionController {
//
//    private final UserService userService;
//    private final SubscriptionService subscriptionService;
//
//    @Autowired
//    public SubscriptionController(UserService userService, SubscriptionService subscriptionService) {
//        this.userService = userService;
//        this.subscriptionService = subscriptionService;
//    }
//
//    @GetMapping
//    public ModelAndView getUpgradePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
//
//        User user = userService.getById(authenticationMetadata.getUserId());
//
//        ModelAndView modelAndView = new ModelAndView();
//
//        modelAndView.setViewName("upgrade");
//        modelAndView.addObject("user", user);
//        modelAndView.addObject("upgradeRequest", UpgradeRequest.builder().build());
//
//        return modelAndView;
//    }
//
////    @PostMapping
//    public String upgrade(@RequestParam("subscription-type") SubscriptionType subscriptionType, UpgradeRequest upgradeRequest, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
//
//        User user = userService.getById(authenticationMetadata.getUserId());
//
//        Transaction upgradeResult = subscriptionService.upgrade(user, subscriptionType, upgradeRequest);
//
//        return "redirect:/transactions/" + upgradeResult.getId();
//    }
//
//    @GetMapping("/history")
//    public ModelAndView getUserSubscriptionsHistory(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
//        User user = userService.getById(authenticationMetadata.getUserId());
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("subscription-history");
//        modelAndView.addObject("user", user);
//
//        return modelAndView;
//    }
//
//
//
//}
