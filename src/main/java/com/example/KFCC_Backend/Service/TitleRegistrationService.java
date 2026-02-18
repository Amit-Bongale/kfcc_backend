package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.DTO.Membership.ApplicationActionRequestDTO;
import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.Enum.TitleApplicationStatus;
import com.example.KFCC_Backend.Enum.UserRoles;
import com.example.KFCC_Backend.ExceptionHandlers.BadRequestException;
import com.example.KFCC_Backend.ExceptionHandlers.ResourceNotFoundException;
import com.example.KFCC_Backend.Repository.Membership.MembershipRepository;
import com.example.KFCC_Backend.Repository.Title.TitleRegistrationDocumentsRepository;
import com.example.KFCC_Backend.Repository.Title.TitleRegistrationRepository;
import com.example.KFCC_Backend.Repository.Users.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Utility.FileStorageUtil;
import com.example.KFCC_Backend.Entity.Title.TitleRegistration;
import com.example.KFCC_Backend.Entity.Title.TitleRegistrationDocuments;
import com.example.KFCC_Backend.Entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TitleRegistrationService {

    @Autowired
    private TitleRegistrationRepository titleRegistrationRepository;

    @Autowired
    private TitleRegistrationDocumentsRepository documentsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    // submit application
    public TitleRegistration submitApplication(TitleRegistration request , CustomUserDetails userDetails, List<MultipartFile> files ) throws IOException{
        Long producerId = userDetails.getUserId();

        Users producer  = usersRepository.findById(producerId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        // check if producer has valid membership
        boolean hasValidMembership = membershipRepository.hasValidMembership(producerId , MembershipStatus.FINAL_APPROVED, LocalDate.now());

        if (!hasValidMembership) {
            throw new BadRequestException(
                    "Valid membership required to apply for title registration"
            );
        }

        if (titleRegistrationRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new BadRequestException("Title already registered or in process");
        }

        TitleRegistration app = new TitleRegistration();
        app.setTitle(request.getTitle().trim());
        app.setTitleInKannada(request.getTitleInKannada());
        app.setProducer(producer);
        app.setDate(request.getDate());
        app.setFirstFilm(request.getFirstFilm());
        app.setInstitution(request.getInstitution());
        app.setLanguage(request.getLanguage());
        app.setPreviouslyRegistered(request.getPreviouslyRegistered());
        app.setPreviouslyRegisteredDetails(request.getPreviouslyRegisteredDetails());
        app.setFilmsByInstitutes(request.getFilmsByInstitutes());
        app.setDirector(request.getDirector());
        app.setMusicDirector(request.getMusicDirector());
        app.setLeadActor(request.getLeadActor());
        app.setCategory(request.getCategory());
        app.setGstNo(request.getGstNo());

        app.setStatus(TitleApplicationStatus.PENDING_PAYMENT);
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        TitleRegistration application =  titleRegistrationRepository.save(app);

        if(files != null) {

            if (files.size() > 5) {
                throw new BadRequestException("Max 5 Documents can be Uploaded");
            }

            for (MultipartFile file : files) {

                if (file.isEmpty()) continue;

                // String folderName = "TitleRegistration/" + application.getId().toString();

                //  UTILITY
                String storedPath = fileStorageUtil.saveFile(
                        "TitleRegistration/Documents",
                        application.getId().toString(),
                        file
                );

                if (storedPath == null) continue;

                TitleRegistrationDocuments doc = new TitleRegistrationDocuments();
                doc.setApplication(application);
                doc.setPath(storedPath);

                documentsRepository.save(doc);
            }
        }

        return application;

    }

    public void markAsPaid(Long titleId) {
        TitleRegistration title = titleRegistrationRepository
                .findById(titleId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not Found"));

        title.setStatus(TitleApplicationStatus.SUBMITTED);
        titleRegistrationRepository.save(title);
    }

    // get Application Details by Id
    public TitleRegistration getApplicationDetailsById(Long applicationId , CustomUserDetails user) {


        TitleRegistration application =  titleRegistrationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        Set<String> roles = user.getRoles();

        boolean isOnlyUser = roles.contains("USER") && roles.contains("PRODUCER") && roles.size() == 2;;

        if (isOnlyUser) {
            Long applicationUserId = application.getProducer().getId();

            if (!applicationUserId.equals(user.getUserId())) {
                throw new AccessDeniedException(
                        "You are not allowed to view this application"
                );
            }
        }

        return  application;
    }


    // Fetch submitted applications for all Roles
    public List<TitleRegistration> getPendingApplications (CustomUserDetails user){

        Set<String> roles = user.getRoles();

       Set<TitleApplicationStatus> allowedStatuses = new HashSet<>();

        for (String role : roles) {
            switch (role) {

                case "STAFF" -> allowedStatuses.add(TitleApplicationStatus.SUBMITTED);

                case "TITLE_COMMITTEE" , "TITLE_COMMITTEE_LEADER" , "TITLE_COMMITTEE_VOTER" -> allowedStatuses.add(TitleApplicationStatus.STAFF_APPROVED);

                case "EC_MEMBER", "SECRETARY" -> allowedStatuses.add(TitleApplicationStatus.TITLE_COMMITTEE_APPROVED);

                default -> {}
            }
        }

        if (allowedStatuses.isEmpty()) {
            throw new AccessDeniedException("UnAuthorized");
        }

        return titleRegistrationRepository
                .findByCurrentStatusIn(allowedStatuses);

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
    public void TitleApplicationAction(Long applicationId, ApplicationActionRequestDTO request , CustomUserDetails user){

        TitleRegistration application = titleRegistrationRepository.findById(applicationId).orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        TitleApplicationStatus currentStatus = application.getStatus();
        Set<String> roles = user.getRoles();

        TitleApplicationStatus newStatus;


        // Staff stage
        if(currentStatus ==  TitleApplicationStatus.SUBMITTED){

            requireRole(roles , "STAFF");

            newStatus = switch (request.getAction()){
                case APPROVE -> TitleApplicationStatus.STAFF_APPROVED;
                case REJECT -> {
                    requireRemarks(request);
                    yield TitleApplicationStatus.STAFF_REJECTED;
                }
                case REMARK -> {
                    requireRemarks(request);
                    application.setRemarkedBy(UserRoles.STAFF);
                    yield TitleApplicationStatus.DRAFT;
                }
                default -> throw new IllegalStateException("Invalid action");
            };

        }

        //  Title Committee STAGE
        else if (currentStatus == TitleApplicationStatus.STAFF_APPROVED) {

            requireRole(roles, "TITLE_COMMITTEE_LEADER");

            newStatus = switch (request.getAction()) {
                case APPROVE -> TitleApplicationStatus.TITLE_COMMITTEE_APPROVED;
                case REJECT -> {
                    requireRemarks(request);
                    yield TitleApplicationStatus.TITLE_COMMITTEE_REJECTED;
                }
                case REMARK -> {
                    requireRemarks(request);
                    application.setRemarkedBy(UserRoles.TITLE_COMMITTEE);
                    yield TitleApplicationStatus.DRAFT;
                }
                default -> throw new BadRequestException("Invalid action");
            };
        }

        else if (currentStatus == TitleApplicationStatus.TITLE_COMMITTEE_APPROVED) {

            requireRole(roles, "SECRETARY");

            newStatus = switch (request.getAction()) {
                case APPROVE -> {
                    application.setAcceptedDate(LocalDate.now());
                    application.setExpireDate(LocalDate.now().plusYears(1));
                    yield TitleApplicationStatus.FINAL_APPROVED;
                }
                case REJECT -> {
                    requireRemarks(request);
                    yield TitleApplicationStatus.EC_COMMITTEE_REJECTED;
                }
                case HOLD -> TitleApplicationStatus.EC_COMMITTEE_HOLD;
                case REMARK -> {
                    requireRemarks(request);
                    application.setRemarkedBy(UserRoles.EC_MEMBER);
                    yield TitleApplicationStatus.DRAFT;
                }
                default -> throw new BadRequestException("Invalid action");
            };
        }

        else {
            throw new BadRequestException( "Application not in actionable state");
        }

        application.setStatus(newStatus);

        if (request.getRemark() != null && !request.getRemark().isBlank()) {
            application.setRemark(request.getRemark());
        }

        titleRegistrationRepository.save(application);


        /* ----- implement a extra table if log is required  in future --------- */

        //        logAction(app, request, user);

    }


    //Update application
    public TitleRegistration updateApplication(
            Long applicationId,
            TitleRegistration request,
            CustomUserDetails userDetails,
            List<MultipartFile> newFiles,
            List<Long> deletedDocumentIds
    ) throws IOException {

        Long producerId = userDetails.getUserId();

        TitleRegistration application = titleRegistrationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application Not Found"));

        // Ensure logged-in producer owns this application
        if (!application.getProducer().getId().equals(producerId)) {
            throw new BadRequestException("You are not authorized to update this application");
        }

        //  Allow update only in editable statuses
        if (!(application.getStatus() == TitleApplicationStatus.PENDING_PAYMENT
                || application.getStatus() == TitleApplicationStatus.DRAFT)) {
            throw new BadRequestException("Application cannot be edited at this stage");
        }

        // If title changed, check uniqueness
        if (!application.getTitle().equalsIgnoreCase(request.getTitle().trim())) {

            if (titleRegistrationRepository.existsByTitleIgnoreCase(request.getTitle().trim())) {
                throw new BadRequestException("Title already registered or in process");
            }

            application.setTitle(request.getTitle().trim());
        }

        
        // Update other fields
        application.setTitleInKannada(request.getTitleInKannada());
        application.setFirstFilm(request.getFirstFilm());
        application.setInstitution(request.getInstitution());
        application.setLanguage(request.getLanguage());
        application.setPreviouslyRegistered(request.getPreviouslyRegistered());
        application.setPreviouslyRegisteredDetails(request.getPreviouslyRegisteredDetails());
        application.setFilmsByInstitutes(request.getFilmsByInstitutes());
        application.setDirector(request.getDirector());
        application.setMusicDirector(request.getMusicDirector());
        application.setLeadActor(request.getLeadActor());
        application.setCategory(request.getCategory());
        application.setGstNo(request.getGstNo());
        application.setUpdatedAt(LocalDateTime.now());

        if (application.getStatus() == TitleApplicationStatus.PENDING_PAYMENT) {
            application.setStatus(TitleApplicationStatus.PENDING_PAYMENT);
        } else {
            application.setStatus(TitleApplicationStatus.SUBMITTED);
            application.setRemark(null);
            application.setRemarkedBy(null);
        }

        TitleRegistration updatedApp = titleRegistrationRepository.save(application);


        // Handle File Uploads
        if (deletedDocumentIds != null && !deletedDocumentIds.isEmpty()) {

            List<TitleRegistrationDocuments> docsToDelete =
                    documentsRepository.findAllById(deletedDocumentIds);

            for (TitleRegistrationDocuments doc : docsToDelete) {

                // delete file from server
                fileStorageUtil.deleteFile(doc.getPath());

                documentsRepository.delete(doc);
            }
        }

        // Check total count
        Long existingCount = documentsRepository.countByApplicationId(applicationId);
        int newFilesCount = newFiles != null ? newFiles.size() : 0;

        if (existingCount + newFilesCount > 5) {
            throw new BadRequestException("Maximum 5 documents allowed");
        }

        // Save new files
        if (newFiles != null) {

            for (MultipartFile file : newFiles) {

                if (file.isEmpty()) continue;

                String storedPath = fileStorageUtil.saveFile(
                        "TitleRegistration/Documents",
                        application.getId().toString(),
                        file
                );

                TitleRegistrationDocuments doc = new TitleRegistrationDocuments();
                doc.setApplication(application);
                doc.setPath(storedPath);

                documentsRepository.save(doc);
            }
        }

        return updatedApp;
    }


    // Get applications by user
    public List<TitleRegistration> getApplicationsByUser(CustomUserDetails user) {
        Long userId = user.getUserId();
        return titleRegistrationRepository.findByProducerIdOrderByCreatedAtDesc(userId);
    }


    //renew titles
    public void reNewTitle(Long id){

        TitleRegistration application = titleRegistrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        LocalDate today = LocalDate.now();
        LocalDate expiry = application.getExpireDate();

        LocalDate renewDate = expiry.isBefore(today)
                ? today.plusYears(1)
                : expiry.plusYears(1);

        application.setExpireDate(renewDate);

        titleRegistrationRepository.save(application);

    }

    // Delete all the records with status Payment Pending
    @Transactional
    public void deleteAllTitlePendingRecords() {
        titleRegistrationRepository
                .deleteByStatusAndCreatedAtBefore(TitleApplicationStatus.PENDING_PAYMENT , LocalDateTime.now());
    }


}