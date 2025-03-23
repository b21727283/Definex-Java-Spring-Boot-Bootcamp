package com.mgumussoy.advancedtaskmanagement.services;

import com.mgumussoy.advancedtaskmanagement.dtos.AuthorityDTO;
import com.mgumussoy.advancedtaskmanagement.exceptions.AuthorityNotFoundException;

public interface AuthorityService {
    AuthorityDTO createAuthority(AuthorityDTO authority);

    void deleteAuthority(Long authorityId) throws AuthorityNotFoundException;

    void updateAuthority(Long authorityId, AuthorityDTO newAuthority) throws AuthorityNotFoundException;

    AuthorityDTO getAuthority(Long authorityId) throws AuthorityNotFoundException;
}
