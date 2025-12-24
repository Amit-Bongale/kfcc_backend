package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.Components.ApplicationFetchConfig;
import com.example.KFCC_Backend.DTO.ApplicationActionRequestDTO;
import com.example.KFCC_Backend.DTO.MembershipApplicationRequestDTO;
import com.example.KFCC_Backend.DTO.MembershipApplicationUpdateRequest;
import com.example.KFCC_Backend.DTO.MembershipApplicationsResponseDTO;
import com.example.KFCC_Backend.Enum.ApplicationAction;
import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.Enum.OwnershipType;
import com.example.KFCC_Backend.Repository.MembershipRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Utility.FileStorageUtil;
import com.example.KFCC_Backend.entity.Membership.MembershipApplication;

import com.example.KFCC_Backend.entity.Membership.Proprietor;
import com.example.KFCC_Backend.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MembershipApplicationService {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Autowired
    private ApplicationFetchConfig applicationFetchConfig;


    private void validateMember(Long membershipId) {
        if (!membershipRepository.existsByMembershipIdAndMembershipStatus(
                membershipId, MembershipStatus.FINAL_APPROVED)) {
            throw new IllegalArgumentException("Invalid proposer/seconder membership");
        }
    }


/*------ Need to fix Images ------------------- */
    public MembershipApplication submitApplication(
            Users user,
            MembershipApplicationRequestDTO request,

            MultipartFile applicantPhoto,
            MultipartFile applicantPan,
            MultipartFile applicantAadhaar,
            MultipartFile applicantAddressProof,
            MultipartFile applicantSignature,
            MultipartFile firmSeal,

            MultipartFile proprietorPan,
            MultipartFile proprietorAadhaar,
            MultipartFile proprietorESignature,

            MultipartFile[] partnerPan,
            MultipartFile[] partnerAadhaar,
            MultipartFile[] partnerSignature,

            MultipartFile partnershipDeed,

            MultipartFile moa,
            MultipartFile aoa
    ) throws IOException {

        // Nominee validation
        if (request.getNominees() == null || request.getNominees().isEmpty() ||
                request.getNominees().size() > 2) {
            throw new IllegalArgumentException("Minimum 1 and maximum 2 nominees allowed");
        }


        validateMember(request.getProposer().getProposerMembershipId());
        validateMember(request.getSeconder().getSeconderMembershipId());


        // Ownership rules

        boolean isProprietor =
                request.getApplicantOwnershipType() == OwnershipType.PROPRIETOR;

        if (isProprietor) {
            if (request.getProprietor() == null) {
                throw new IllegalArgumentException("Proprietor details are mandatory");
            }
            if (request.getPartners() != null && !request.getPartners().isEmpty()) {
                throw new IllegalArgumentException("Proprietor cannot have partners");
            }
        } else {
            if (request.getPartners() != null && request.getPartners().size() > 6) {
                throw new IllegalArgumentException("Maximum 6 partners allowed");
            }

//            if (partnershipDeed == null || partnershipDeed.isEmpty()) {
//                throw new IllegalArgumentException("Partnership deed is mandatory");
//            }

        }

            /* ---------------- CREATE APPLICATION ---------------- */

        MembershipApplication application = new MembershipApplication();
        application.setUser(user);
        application.setDate(LocalDate.now());
        application.setApplicantMembershipCategory(request.getApplicantMembershipCategory());
        application.setApplicantFirmName(request.getApplicantFirmName());
        application.setApplicantOwnershipType(request.getApplicantOwnershipType());
        application.setApplicantGstNo(request.getApplicantGstNo());

        application.setApplicantAddressLine1(request.getApplicantAddressLine1());
        application.setApplicantAddressLine2(request.getApplicantAddressLine2());
        application.setApplicantDistrict(request.getApplicantDistrict());
        application.setApplicantState(request.getApplicantState());

        application.setMembershipFee(request.getMembershipFee());
        application.setKalyanNidhi(request.getKalyanNidhi());
        application.setTotalAmount(
                request.getMembershipFee() + request.getKalyanNidhi()
        );

        application.setMembershipStatus(MembershipStatus.SUBMITTED);

        final MembershipApplication finalApp = membershipRepository.save(application);


        /* ---------------- FILE UPLOADS ---------------- */

//        int totalFiles =
//                1 + // applicant photo
//                        5 + // applicant docs
//                        (partnerPan != null ? partnerPan.length * 3 : 0) + // pan + aadhaar + signature
//                        3; // deed + moa + aoa
//
//        if (totalFiles > 30) {
//            throw new IllegalArgumentException("Too many documents uploaded");
//        }

        String appFolder = "APP-" + String.format("%06d", application.getApplicationId());

        String applicantImgPath =  fileStorageUtil.saveFile(appFolder, "applicant", applicantPhoto);
        application.setApplicantImage(applicantImgPath);


        if (request.getProprietor() != null) {

            Proprietor proprietor = request.getProprietor();
            proprietor.setMembershipApplication(application);

            proprietor.setProprietorPanImg(
                    fileStorageUtil.saveFile(appFolder, "proprietor", proprietorPan)
            );
            proprietor.setProprietorAadhaarImg(
                    fileStorageUtil.saveFile(appFolder, "proprietor", proprietorAadhaar)
            );
            proprietor.setProprietorESignature(
                    fileStorageUtil.saveFile(appFolder, "proprietor" , proprietorESignature)
            );

            application.setProprietor(proprietor);
        }

        /* ---- applicant Documents ---*/

        application.setApplicantPan(
                fileStorageUtil.saveFile(appFolder, "applicant/pan", applicantPan) );
        application.setApplicantAadhaar(
                fileStorageUtil.saveFile(appFolder, "applicant/aadhaar", applicantAadhaar));
        application.setApplicantAddressProof(
                fileStorageUtil.saveFile(appFolder, "applicant/address-proof", applicantAddressProof));
        application.setApplicantSignature(
                fileStorageUtil.saveFile(appFolder, "applicant/signature", applicantSignature));
        application.setFirmSeal(
                fileStorageUtil.saveFile(appFolder, "applicant/firm-seal", firmSeal));


        if (moa != null) {
            application.setMoa(fileStorageUtil.saveFile(appFolder, "ownership/moa", moa));

        }
        if (aoa != null) {
            application.setAoa(fileStorageUtil.saveFile(appFolder, "ownership/aoa", aoa));
        }

        /* ------------------  PARTNERS DOCUMENTS ------------ */

//        if (!isProprietor && request.getPartners() != null) {
//
//            if (partnerPan.length != request.getPartners().size()
//                    || partnerAadhaar.length != request.getPartners().size()
//                    || partnerSignature.length != request.getPartners().size()) {
//                throw new IllegalArgumentException("Partner document count mismatch");
//            }
//
//            application.setPartnershipDeed(
//                    fileStorageUtil.saveFile(appFolder, "ownership/partnership-deed", partnershipDeed)
//            );
//
//            application.getPartners().clear();
//
//            for (int i = 0; i < request.getPartners().size(); i++) {
//                Partners partner = request.getPartners().get(i);
//                partner.setMembershipApplication(application);
//
//                partner.setPartnerPanImg(
//                        fileStorageUtil.saveFile(appFolder, "partners/" + i + "/pan", partnerPan[i])
//                );
//                partner.setPartnerAadhaarImg(
//                        fileStorageUtil.saveFile(appFolder, "partners/" + i + "/aadhaar", partnerAadhaar[i])
//                );
//                partner.setPartnerESignature(
//                        fileStorageUtil.saveFile(appFolder, "partners/" + i + "/signature", partnerSignature[i])
//                );
//
//                application.getPartners().add(partner);
//            }
//        }

        /* ---------------- CHILD ENTITIES ---------------- */

        // IMPORTANT: do NOT replace the list
        finalApp.getNominee().clear();

        for (var nominee : request.getNominees()) {
            nominee.setMembershipApplication(finalApp);
            finalApp.getNominee().add(nominee);
        }

        if (request.getPartners() != null) {
            finalApp.getPartners().clear();

            for (var partner : request.getPartners()) {
                partner.setMembershipApplication(finalApp);
                finalApp.getPartners().add(partner);
            }
        }

        request.getProposer().setMembershipApplication(finalApp);
        request.getSeconder().setMembershipApplication(finalApp);

        finalApp.setProposer(request.getProposer());
        finalApp.setSeconder(request.getSeconder());


        return membershipRepository.save(finalApp);

    }


    // get pending applications for approval [staff , ONM, EC , Secretary]
    public List<MembershipApplicationsResponseDTO>  getPendingApplications (CustomUserDetails user){

        Set<String> roles = user.getRoles();
        System.out.println(roles);


        Set<MembershipStatus> allowedStatuses =
                applicationFetchConfig.getAllowedStatuses(roles);

        System.out.println("allowed status" + allowedStatuses);

        if (allowedStatuses.isEmpty()) {
            return List.of();
        }



        return membershipRepository
                .findByCurrentStatusIn(allowedStatuses)
                .stream()
                .map(app -> new MembershipApplicationsResponseDTO(
                        app.getApplicationId(),
                        app.getUser().getId(),
                        app.getUser().getFirstName() + " " + app.getUser().getLastName(),
                        app.getUser().getMobileNo(),
                        app.getApplicantMembershipCategory(),
                        app.getMembershipStatus(),
                        app.getSubmittedAt(),
                        app.getRemark()
                ))
                .collect(Collectors.toList());
    }



    // get application details on ID
    public MembershipApplication getApplicationDetailsByID(Long applicationId) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        CustomUserDetails currentUser =
                (CustomUserDetails) authentication.getPrincipal();

        MembershipApplication application =
                membershipRepository.findByApplicationId(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException("Membership application not found"));

        Set<String> roles = currentUser.getRoles();

        boolean isOnlyUser =  roles.size() == 1 && roles.contains("USER");

        if (isOnlyUser) {
            Long applicationUserId = application.getUser().getId();

            if (!applicationUserId.equals(currentUser.getUserId())) {
                throw new AccessDeniedException(
                        "You are not allowed to view this application"
                );
            }
        }

        return application;

    }

    /* ---------- Application Action Helpers --------------- */

    private void requireRole(Set<String> roles, String required) {
        if (!roles.contains(required)) {
            throw new AccessDeniedException("Unauthorized");
        }
    }

    private void requireRemarks(ApplicationActionRequestDTO request) {
        if (request.getRemark() == null || request.getRemark().isBlank()) {
            throw new IllegalArgumentException("Remarks required");
        }
    }


    // Approve / Reject /  Remark Applications
    @Transactional
    public void MembershipApplicationAction(Long applicationId, ApplicationActionRequestDTO request , CustomUserDetails user){

        MembershipApplication application = membershipRepository.findByApplicationId(applicationId).orElseThrow(() -> new RuntimeException("Application not found"));

        MembershipStatus currentStatus = application.getMembershipStatus();
        Set<String> roles = user.getRoles();

        MembershipStatus newStatus;


        // Staff stage
        if(currentStatus ==  MembershipStatus.SUBMITTED){

            requireRole(roles , "STAFF");

            newStatus = switch (request.getAction()){
                case APPROVE -> MembershipStatus.STAFF_APPROVED;
                case REJECT -> {
                    requireRemarks(request);
                    yield MembershipStatus.STAFF_REJECTED;
                }
                case REMARK -> {
                    requireRemarks(request);
                    yield MembershipStatus.STAFF_REMARKED;
                }
                default -> throw new IllegalStateException("Invalid action");
            };
        }

        //  ONM STAGE
        else if (currentStatus == MembershipStatus.STAFF_APPROVED) {

            requireRole(roles, "ONM_COMMITTEE_LEADER");

            newStatus = switch (request.getAction()) {
                case APPROVE -> MembershipStatus.ONM_APPROVED;
                case REJECT -> {
                    requireRemarks(request);
                    yield MembershipStatus.ONM_REJECTED;
                }
                case REMARK -> {
                    requireRemarks(request);
                    yield MembershipStatus.DRAFT;
                }
                default -> throw new IllegalStateException("Invalid action");
            };
        }

        else if (currentStatus == MembershipStatus.ONM_APPROVED) {

            requireRole(roles, "SECRETARY");

            newStatus = switch (request.getAction()) {
                case APPROVE -> MembershipStatus.FINAL_APPROVED;
                case REJECT -> {
                    requireRemarks(request);
                    yield MembershipStatus.EC_REJECTED;
                }
                case HOLD -> MembershipStatus.EC_HOLD;
                case REMARK -> {
                    requireRemarks(request);
                    yield MembershipStatus.DRAFT;
                }
                default -> throw new IllegalStateException("Invalid action");
            };
        }

        else {
            throw new IllegalStateException(
                    "Application not in actionable state"
            );
        }


        application.setMembershipStatus(newStatus);

        if (request.getRemark() != null && !request.getRemark().isBlank()) {
            application.setRemark(request.getRemark());
        }

        membershipRepository.save(application);


        /* ----- implement a extra table if log is required  in future --------- */

//        logAction(app, request, user);

        //    private void logAction(
//            MembershipApplication app,
//            ApplicationActionRequest request,
//            CustomUserDetails user
//    ) {
//        ApplicationActionLog log = new ApplicationActionLog();
//        log.setApplication(app);
//        log.setAction(request.getAction());
//        log.setRemarks(request.getRemarks());
//        log.setActionBy(user.getUser());
//        log.setActionAt(LocalDateTime.now());
//
//        actionLogRepository.save(log);
//    }


    }




    public void updateApplication(
            Long applicationId,
            MembershipApplicationUpdateRequest request,
            CustomUserDetails userDetails
    ) {

        MembershipApplication application =
                membershipRepository.findById(applicationId)
                        .orElseThrow(() -> new RuntimeException("Application not found"));

        // Ownership validation
        if (!application.getUser().getId().equals(userDetails.getUserId())) {
            throw new AccessDeniedException("You cannot edit this application");
        }

        //  Status validation
        if (application.getMembershipStatus() != MembershipStatus.DRAFT)  {
            throw new IllegalStateException("Applications cannot be edited");
        }

        // Update simple fields
        application.setApplicantDistrict(request.getDistrict());
        application.setApplicantState(request.getState());
        application.setApplicantPinCode(request.getPinCode());
        application.setApplicantGstNo(request.getGstNo());

        application.setApplicantMembershipCategory(request.getCategory());
        application.setApplicantOwnershipType(request.getOwnershipType());
        application.setApplicantFirmName(request.getFirmName());
        application.setApplicantAddressLine1(request.getAddressLine1());
        application.setApplicantAddressLine2(request.getAddressLine2());


        // Nominees
        if (request.getNominees() != null) {
            if (request.getNominees().size() > 2)
                throw new IllegalArgumentException("Max 2 nominees allowed");

//            application.updateNominees(request.getNominees());
        }

        //  Partners
        if (request.getOwnershipType() != OwnershipType.PROPRIETOR) {
            if (request.getPartners() != null && request.getPartners().size() > 6)
                throw new IllegalArgumentException("Max 6 partners allowed");

//            application.updatePartners(request.getPartners());

        } else {
//            application.clearPartners();
        }

        membershipRepository.save(application);

    }




}