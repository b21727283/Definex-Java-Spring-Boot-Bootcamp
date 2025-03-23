package com.mgumussoy.advancedtaskmanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "AuthorityBuilder")
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "authorities")
public class Authority extends BaseEntity implements GrantedAuthority {
    @Column(unique = true, nullable = false)
    private String authority;

    @ManyToMany(mappedBy = "authorities", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserEntity> users = new ArrayList<>();

    @Override
    public String getAuthority() {
        return authority;
    }

    public static class AuthorityBuilder {
        private Long id;

        public AuthorityBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public Authority build() {
            Authority authority = new Authority();
            authority.setId(this.id);
            authority.setAuthority(this.authority);
            authority.setUsers(this.users$value);
            return authority;
        }
    }
}
