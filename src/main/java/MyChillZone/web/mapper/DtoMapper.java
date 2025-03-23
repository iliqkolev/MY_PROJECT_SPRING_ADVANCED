package MyChillZone.web.mapper;

import MyChillZone.user.model.User;
import MyChillZone.web.dto.UserEditRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static UserEditRequest mapUserToUserEditRequest(User user){
        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .country(String.valueOf(user.getCountry()))
                .profilePicture(user.getProfilePicture())
                .build();
    }


}
