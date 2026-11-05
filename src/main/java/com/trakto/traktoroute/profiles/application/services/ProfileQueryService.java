package com.trakto.traktoroute.profiles.application.services;

import com.trakto.traktoroute.profiles.application.queries.GetAllProfilesQuery;
import com.trakto.traktoroute.profiles.application.queries.GetProfileByIdQuery;
import com.trakto.traktoroute.profiles.application.queries.GetProfileByUserReferenceIdQuery;
import com.trakto.traktoroute.profiles.domain.model.aggregates.Profile;
import com.trakto.traktoroute.profiles.domain.repositories.ProfileRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile handle(GetProfileByIdQuery query) {
        return profileRepository.findById(query.profileId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Profile not found")
                );
    }

    public Profile handle(GetProfileByUserReferenceIdQuery query) {
        return profileRepository.findByUserReferenceId(
                        query.userReferenceId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Profile not found for this user"
                        )
                );
    }

    public List<Profile> handle(GetAllProfilesQuery query) {
        return profileRepository.findAll();
    }
}