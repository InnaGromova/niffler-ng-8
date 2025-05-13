package guru.qa.niffler.data.extractor;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.values.AuthorityType;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AuthUserWithAuthoritiesExtractor implements ResultSetExtractor<List<AuthUserEntity>> {
    @Override
    public List<AuthUserEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, AuthUserEntity> userMap = new LinkedHashMap<>();
        while (rs.next()) {
            UUID userId = rs.getObject("user_id", UUID.class);
            AuthUserEntity user = userMap.get(userId);

            if (user == null) {
                user = new AuthUserEntity();
                user.setId(userId);
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEnabled(rs.getBoolean("enabled"));
                user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                user.setAuthorities(new ArrayList<>());
                userMap.put(userId, user);
            }

            UUID authorityId = rs.getObject("authority_id", UUID.class);
            if (authorityId != null) {
                AuthAuthorityEntity authority = new AuthAuthorityEntity();
                authority.setId(authorityId);
                authority.setAuthority(AuthorityType.valueOf(rs.getString("authority")));
                authority.setUser(user);
                user.getAuthorities().add(authority);
            }
        }

        return new ArrayList<>(userMap.values());
    }
}
