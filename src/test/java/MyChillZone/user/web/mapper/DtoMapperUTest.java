package MyChillZone.user.web.mapper;

import MyChillZone.user.model.User;
import MyChillZone.web.dto.UserEditRequest;
import MyChillZone.web.mapper.DtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {
    @Test
    void givenHappyPath_whenMappingUserToUserRequest(){
        //Given
        User user = User.builder()
                .firstName("Vik")
                .lastName("Aleksandrov")
                .email("vik@abv.bg")
                .profilePicture("www.image.com")
                .build();

        //When
        UserEditRequest resultDto = DtoMapper.mapUserToUserEditRequest(user);

        //Then
        assertEquals(user.getFirstName(), resultDto.getFirstName());
        assertEquals(user.getLastName(), resultDto.getLastName());
        assertEquals(user.getEmail(), resultDto.getEmail());
        assertEquals(user.getProfilePicture(), resultDto.getProfilePicture());
    }
}
