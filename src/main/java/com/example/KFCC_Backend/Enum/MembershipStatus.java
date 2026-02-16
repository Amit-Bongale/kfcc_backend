package com.example.KFCC_Backend.Enum;

public enum MembershipStatus {

    PENDING_PAYMENT,

    DRAFT,                 // user editable (remarked state)
    SUBMITTED,              // submitted by user && payment is success
    STAFF_REJECTED,
    STAFF_APPROVED,

    ONM_REJECTED,
    ONM_APPROVED,

    EC_REJECTED,
    EC_HOLD,

    FINAL_APPROVED
}
