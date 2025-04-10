package MyChillZone;


import MyChillZone.user.model.Country;
import MyChillZone.user.model.User;
import MyChillZone.user.model.UserRole;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class TestBuilder {

    public static  User aRandomUser(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("User")
                .password("123123")
                .userRole(UserRole.USER)
                .country(Country.BULGARIA)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        return user;
    }



















}
