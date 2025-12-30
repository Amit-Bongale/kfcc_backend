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
import com.example.KFCC_Backend.entity.Title.TitleRegistration;
import com.example.KFCC_Backend.entity.Title.TitleRegistrationDocuments;
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

        app.setStatus(TitleApplicationStatus.SUBMITTED);
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        TitleRegistration application =  titleRegistrationRepository.save(app);


        if(files.size() > 5){
            throw new BadRequestException("Max 5 Documents can be Uploaded");
        }

        for (MultipartFile file : files) {

            if (file.isEmpty()) continue;

//            String folderName = "TitleRegistration/" + application.getId().toString();

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


        return application;

    }

    // get Application Details by Id
    public TitleRegistration getApplicationDetailsById(Long applicationId) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        CustomUserDetails currentUser =
                (CustomUserDetails) authentication.getPrincipal();


        TitleRegistration application =  titleRegistrationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        Set<String> roles = currentUser.getRoles();

        boolean isOnlyUser = roles.contains("USER") && roles.contains("PRODUCER") && roles.size() == 2;;

        if (isOnlyUser) {
            Long applicationUserId = application.getProducer().getId();

            if (!applicationUserId.equals(currentUser.getUserId())) {
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


    public TitleRegistration updateApplication(Long applicationId , TitleRegistration request){

        TitleRegistration application = titleRegistrationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not Found"));

        //update to be implemented

        return application;

    }


    public List<TitleRegistration> getApplicationsByUser(CustomUserDetails user) {
        Long userId = user.getUserId();
        return titleRegistrationRepository.findByProducerIdOrderByCreatedAtDesc(userId);
    }
}

