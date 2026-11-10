package com.trakto.traktoroute.profiles.application.acl;

import com.trakto.traktoroute.profiles.domain.model.valueobjects.ProfileId;
import com.trakto.traktoroute.profiles.domain.repositories.ProfileRepository;
import com.trakto.traktoroute.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {

    private final ProfileRepository profileRepository;

    public ProfilesContextFacadeImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public boolean existsProfile(UUID profileId){
        return profileRepository.existsById(new ProfileId(profileId));
    }
}
