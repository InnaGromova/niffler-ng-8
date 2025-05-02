package guru.qa.niffler.data.entity.spend;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Getter
@Setter
public class UserEntity implements Serializable {
    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String firstname;
    private String surname;
    private String fullname;
    private byte[] photo;
    private byte[] photoSmall;

    public static UserEntity fromJson(UserJson json) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(json.id());
        userEntity.setUsername(json.username());
        userEntity.setSurname(json.surname());
        userEntity.setFullname(json.fullname());
        userEntity.setFirstname(json.firstname());
        userEntity.setCurrency(json.currency());
        userEntity.setPhoto(json.photo() != null && !json.photo().isEmpty() ? new String(json.photo().getBytes(), StandardCharsets.UTF_8).getBytes() : null);
        userEntity.setPhotoSmall(json.photoSmall() != null && !json.photoSmall().isEmpty() ? new String(json.photoSmall().getBytes(), StandardCharsets.UTF_8).getBytes() : null);
        return userEntity;
    }
}
