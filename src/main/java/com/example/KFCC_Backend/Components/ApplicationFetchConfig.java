package com.example.KFCC_Backend.Components;

import com.example.KFCC_Backend.Enum.MembershipStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ApplicationFetchConfig {
    private static final Map<String, Set<MembershipStatus>> ROLE_STATUS_MAP =
            Map.of(
                    "STAFF", Set.of(MembershipStatus.SUBMITTED),
                    "ONM_COMMITTEE", Set.of(MembershipStatus.STAFF_APPROVED),
                    "ONM_COMMITTEE_LEADER", Set.of(MembershipStatus.STAFF_APPROVED),
                    "EC_MEMBER", Set.of(MembershipStatus.ONM_APPROVED),
                    "SECRETARY", Set.of(MembershipStatus.ONM_APPROVED),
                    "PRESIDENT" , Set.of(MembershipStatus.ONM_APPROVED)
            );

    public Set<MembershipStatus> getAllowedStatuses(Set<String> roles) {
        return roles.stream()
                .filter(ROLE_STATUS_MAP::containsKey)
                .flatMap(role -> ROLE_STATUS_MAP.get(role).stream())
                .collect(Collectors.toSet());
    }

}
