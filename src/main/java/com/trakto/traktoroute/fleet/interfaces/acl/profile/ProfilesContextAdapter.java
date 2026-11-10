package com.trakto.traktoroute.fleet.interfaces.acl.profile;

import com.trakto.traktoroute.fleet.application.acl.profile.ProfileExistencePort;
import com.trakto.traktoroute.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfilesContextAdapter implements ProfileExistencePort {

    private final ProfilesContextFacade profilesContextFacade;

    public ProfilesContextAdapter(ProfilesContextFacade profilesContextFacade) {
        this.profilesContextFacade = profilesContextFacade;
    }

    @Override
    public boolean profileExists(UUID profileId) {
        return profilesContextFacade.existsProfile(profileId);
    }
}