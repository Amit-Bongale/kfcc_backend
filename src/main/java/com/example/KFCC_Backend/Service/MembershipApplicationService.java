package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.DTO.MembershipApplicationRequest;
import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.Repository.MembershipRepository;
import com.example.KFCC_Backend.Utility.FileStorageUtil;
import com.example.KFCC_Backend.entity.Membership.MembershipApplication;
import com.example.KFCC_Backend.entity.Membership.Partners;
import com.example.KFCC_Backend.entity.Membership.Proprietor;
import com.example.KFCC_Backend.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class MembershipApplicationService {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private FileStorageUtil fileStorageUtil;


    private void validateMember(Long membershipId) {
        if (!membershipRepository.existsByMembershipIdAndMembershipStatus(
                membershipId, MembershipStatus.FINAL_APPROVED)) {
            throw new IllegalArgumentException("Invalid proposer/seconder membership");
        }
    }



    public MembershipApplication submitApplication(
            Users user,
            MembershipApplicationRequest request,

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
                "PROPRIETOR".equalsIgnoreCase(request.getApplicantOwnershipType());

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


}
