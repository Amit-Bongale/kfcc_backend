package com.example.KFCC_Backend.Enum;

public enum MembershipStatus {

    PENDING_PAYMENT,

    DRAFT,                 // user editable
    SUBMITTED,              // submitted by user && payment is sucess
    STAFF_REJECTED,
    STAFF_REMARKED,
    STAFF_APPROVED,

    ONM_VOTING,
    ONM_REJECTED,
    ONM_REMARKED,
    ONM_APPROVED,

    EC_REJECTED,
    EC_REMARKED,
    EC_HOLD,

    FINAL_APPROVED
}
