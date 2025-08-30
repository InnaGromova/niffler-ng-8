package guru.qa.niffler.model;

import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.Authority;

import java.util.UUID;

public record AuthorityJson(
    UUID id,
    UUID userId,
    Authority authority
){
    public AuthorityJson fromEntity(AuthAuthorityEntity entity) {
        return new AuthorityJson(
                entity.getId(),
                entity.getUser().getId(),
                entity.getAuthority()
        );
    }
}
