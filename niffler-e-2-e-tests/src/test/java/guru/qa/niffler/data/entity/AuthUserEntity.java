package guru.qa.niffler.data.entity;
import guru.qa.niffler.model.UserJson;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.util.UUID;
@Getter
@Setter
public class AuthUserEntity implements Serializable {
    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    public static AuthUserEntity fromJson(UserJson json) {
        AuthUserEntity au = new AuthUserEntity();
        au.setUsername(json.username());
        au.setPassword(pe.encode(json.password()));
        au.setEnabled(true);
        au.setAccountNonExpired(true);
        au.setAccountNonLocked(true);
        au.setCredentialsNonExpired(true);
        return au;
    }
}
