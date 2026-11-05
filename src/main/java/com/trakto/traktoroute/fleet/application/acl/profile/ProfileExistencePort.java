package com.trakto.traktoroute.fleet.application.acl.profile;

import java.util.UUID;

public interface ProfileExistencePort {
    boolean profileExists(UUID profileId);
}
