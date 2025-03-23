package com.mgumussoy.advancedtaskmanagement.services.imps;

import com.mgumussoy.advancedtaskmanagement.dtos.AuthorityDTO;
import com.mgumussoy.advancedtaskmanagement.entities.Authority;
import com.mgumussoy.advancedtaskmanagement.exceptions.AuthorityNotFoundException;
import com.mgumussoy.advancedtaskmanagement.repositories.AuthorityRepository;
import com.mgumussoy.advancedtaskmanagement.services.AuthorityService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImp implements AuthorityService {
    private final AuthorityRepository authorityRepository;

    public AuthorityServiceImp(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    @Transactional
    public AuthorityDTO createAuthority(AuthorityDTO authority) {
        Authority newAuthority = Authority.builder()
                .authority(authority.getAuthority())
                .build();
        Authority savedAuthority = authorityRepository.save(newAuthority);
        return AuthorityDTO.builder()
                .id(savedAuthority.getId())
                .authority(savedAuthority.getAuthority())
                .build();
    }

    @Override
    @Transactional
    public void deleteAuthority(Long authorityId) throws AuthorityNotFoundException {
        Authority authority = findAuthorityById(authorityId);
        authority.setDeleted(true);
        authorityRepository.save(authority);
    }

    @Override
    @Transactional
    public void updateAuthority(Long authorityId, AuthorityDTO updatedAuthorityDTO) throws AuthorityNotFoundException {
        Authority updatedAuthority = findAuthorityById(authorityId);
        updatedAuthority.setAuthority(updatedAuthorityDTO.getAuthority());
        authorityRepository.save(updatedAuthority);
    }

    @Override
    public AuthorityDTO getAuthority(Long authorityId) throws AuthorityNotFoundException {
        Authority authority = findAuthorityById(authorityId);
        return AuthorityDTO.builder()
                .id(authorityId)
                .authority(authority.getAuthority())
                .build();
    }

    private Authority findAuthorityById(Long authorityId) throws AuthorityNotFoundException {
        Authority authority = authorityRepository.findById(authorityId).orElseThrow(AuthorityNotFoundException::new);
        if (authority.isDeleted()) throw new AuthorityNotFoundException();
        return authority;
    }
}
