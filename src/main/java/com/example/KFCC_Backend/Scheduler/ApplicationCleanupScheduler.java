package com.example.KFCC_Backend.Scheduler;

import com.example.KFCC_Backend.Service.MembershipApplicationService;
import com.example.KFCC_Backend.Service.TitleRegistrationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ApplicationCleanupScheduler {

    @Autowired
    private MembershipApplicationService membershipApplicationService;

    @Autowired
    private TitleRegistrationService titleRegistrationService;

    //clean all old payment pending applications at night 2AM
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void removeExpiredApplications(){
        membershipApplicationService.deleteAllMembershipPendingRecords();
        titleRegistrationService.deleteAllTitlePendingRecords();
    }


}