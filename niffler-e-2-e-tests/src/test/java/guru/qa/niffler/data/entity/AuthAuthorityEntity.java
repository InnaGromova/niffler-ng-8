package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.UserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;
@Getter
@Setter
public class AuthAuthorityEntity implements Serializable {
    private UUID id;
    private String authority;
}

