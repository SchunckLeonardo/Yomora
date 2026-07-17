package com.yomora.social.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository {
    CommunityActivity save(CommunityActivity activity);

    Optional<CommunityActivity> findById(UUID activityId);

    List<CommunityActivity> list(UUID recipientId, int limit);
}
