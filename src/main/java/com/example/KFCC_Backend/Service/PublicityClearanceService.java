package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.DTO.Membership.ApplicationActionRequestDTO;
import com.example.KFCC_Backend.DTO.PublicityClearance.PublicityClearanceRequestDTO;
import com.example.KFCC_Backend.DTO.PublicityClearance.TitleRegistrationPublicityDTO;
import com.example.KFCC_Backend.DTO.PublicityClearance.PublicityClearanceResponseDTO;
import com.example.KFCC_Backend.Enum.PublicityApplicationStatus;
import com.example.KFCC_Backend.Enum.TitleApplicationStatus;
import com.example.KFCC_Backend.ExceptionHandlers.BadRequestException;
import com.example.KFCC_Backend.ExceptionHandlers.ResourceNotFoundException;
import com.example.KFCC_Backend.Repository.PublicityClearance.PublicityClearanceRepository;
import com.example.KFCC_Backend.Repository.Title.TitleRegistrationRepository;
import com.example.KFCC_Backend.Repository.Users.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Utility.FileStorageUtil;
import com.example.KFCC_Backend.entity.PublicityClearance.PublicityClearanceApplication;
import com.example.KFCC_Backend.entity.PublicityClearance.PublicityClearanceDocuments;
import com.example.KFCC_Backend.entity.Title.TitleRegistration;
import com.example.KFCC_Backend.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PublicityClearanceService {

    @Autowired
    private PublicityClearanceRepository publicityClearanceRepository;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Autowired
    private TitleRegistrationRepository titleRegistrationRepository;

    @Autowired
    private UsersRepository usersRepository;

    // fetch all Titles by user
    public List<PublicityClearanceResponseDTO> getEligibleTitlesForClearance(
            CustomUserDetails user
    ) {

        List<Object[]> rows =
                publicityClearanceRepository
                        .findFinalApprovedTitlesWithPublicityStatus(user.getUserId());

        return rows.stream()
                .map(row -> {

                    TitleRegistration t = (TitleRegistration) row[0];

                    PublicityApplicationStatus status =
                            row[1] == null
                                    ? PublicityApplicationStatus.NOT_SUBMITTED
                                    : (PublicityApplicationStatus) row[1];

                    Long publicityClearanceId = row[2] == null ? null : (Long) row[2];

                    TitleRegistrationPublicityDTO titleDto =
                            new TitleRegistrationPublicityDTO(
                                    t.getId(),
                                    t.getTitle(),
                                    t.getTitleInKannada(),
                                    t.getAcceptedDate(),
                                    t.getCategory(),
                                    t.getCreatedAt(),
                                    t.getDirector(),
                                    t.getExpireDate(),
                                    t.getLanguage(),
                                    t.getLeadActor(),
                                    t.getMusicDirector()
                            );

                    return new PublicityClearanceResponseDTO(titleDto, publicityClearanceId , status);

                })
                .toList();
    }


    //apply for Publicity Clearance
    @Transactional
    public PublicityClearanceApplication applyClearance(Long titleId, PublicityClearanceRequestDTO request , CustomUserDetails user) {

        TitleRegistration title = titleRegistrationRepository
                .findById(titleId).orElseThrow(() -> new ResourceNotFoundException("Application Not found"));

        Users producer = usersRepository.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Producer not found"));

        if (!title.getProducer().getId().equals(producer.getId())) {
            throw new BadRequestException("Only the owner can apply for Clearance");
        }

        // need to handle if exist;
        boolean isPresent = publicityClearanceRepository.existsByTitle_Id(titleId);
        if (isPresent){
            throw new BadRequestException("Publicity Clearance Application Already Exists");
        }

        if(title.getStatus() != TitleApplicationStatus.FINAL_APPROVED){
            throw new BadRequestException("Title not yet Approved");
        }

        PublicityClearanceApplication application = new PublicityClearanceApplication();

        application.setTitle(title);
        application.setProducer(producer);
        application.setStatus(PublicityApplicationStatus.SUBMITTED);
        application.setCbcfCertificateNo(request.getCbcfCertificateNo());
        application.setCbcfCertificateDate(request.getCbcfCertificateDate());

        saveMaterials(application, request.getDocuments());

        return  publicityClearanceRepository.save(application);
    }

    private void saveMaterials(
            PublicityClearanceApplication application,
            Map<PublicityClearanceDocuments.PublicityDocumentType, List<MultipartFile>> materials
    ) {

        if (materials == null || materials.isEmpty()) return;

        for (Map.Entry<
                PublicityClearanceDocuments.PublicityDocumentType,
                List<MultipartFile>> entry : materials.entrySet()) {

            for (MultipartFile file : entry.getValue()) {

                String storedPath;
                try {
                    storedPath = fileStorageUtil.saveFile(
                            "publicityClearance",
                            application.getTitle().getId().toString(),
                            file
                    );
                } catch (IOException e) {
                    throw new RuntimeException("File save failed", e);
                }

                PublicityClearanceDocuments doc = new PublicityClearanceDocuments();
                doc.setApplication(application);
                doc.setDocumentType(entry.getKey());
                doc.setFilePath(storedPath);

                application.getDocuments().add(doc);
            }
        }
    }


    // Fetch submitted applications for all Roles
    public List<PublicityClearanceResponseDTO> getPendingApplications (CustomUserDetails user){

        Set<String> roles = user.getRoles();

        Set<PublicityApplicationStatus> allowedStatuses = new HashSet<>();

        for (String role : roles) {
            switch (role) {

                case "STAFF" -> allowedStatuses.add(PublicityApplicationStatus.SUBMITTED);

                case "VP_PRODUCER" -> allowedStatuses.add(PublicityApplicationStatus.STAFF_APPROVED);

                case "SECRETARY" -> allowedStatuses.add(PublicityApplicationStatus.VP_APPROVED);

                case "MANAGER" -> allowedStatuses.add(PublicityApplicationStatus.SECRETARY_APPROVED);

                default -> {}
            }
        }

        if (allowedStatuses.isEmpty()) {
            throw new AccessDeniedException("UnAuthorized");
        }

        return publicityClearanceRepository
                .findByCurrentStatusIn(allowedStatuses)
                .stream()
                .map(app -> new PublicityClearanceResponseDTO(
                        mapToTitleRegistrationPublicityDTO(app.getTitle()),
                        app.getId(),                     // publicityClearanceId
                        app.getStatus()               // current status
                ))
                .toList();

    }

    private TitleRegistrationPublicityDTO mapToTitleRegistrationPublicityDTO(
            TitleRegistration t
    ) {
        return new TitleRegistrationPublicityDTO(
                t.getId(),
                t.getTitle(),
                t.getTitleInKannada(),
                t.getAcceptedDate(),
                t.getCategory(),
                t.getCreatedAt(),
                t.getDirector(),
                t.getExpireDate(),
                t.getLanguage(),
                t.getLeadActor(),
                t.getMusicDirector()
        );
    }


    // get application details on ID
    


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


    //Application Action
    public void applicationAction(Long applicationId , ApplicationActionRequestDTO request , CustomUserDetails user){

        PublicityClearanceApplication app = publicityClearanceRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found") );

        PublicityApplicationStatus currentStatus = app.getStatus();

        Set<String> UserRoles = user.getRoles();

        PublicityApplicationStatus newStatus;

        // Staff stage
        if(currentStatus ==  PublicityApplicationStatus.SUBMITTED){

            requireRole(UserRoles , "STAFF");

            newStatus = switch (request.getAction()){
                case APPROVE -> PublicityApplicationStatus.STAFF_APPROVED;
                case REJECT -> {
                    requireRemarks(request);
                    yield PublicityApplicationStatus.STAFF_REJECTED;
                }
                case REMARK -> {
                    requireRemarks(request);
                    app.setRemarkedBy("STAFF");
                    yield PublicityApplicationStatus.DRAFT;
                }
                default -> throw new IllegalStateException("Invalid action");
            };
        }

        //  VP STAGE
        else if (currentStatus == PublicityApplicationStatus.STAFF_APPROVED) {

            requireRole(UserRoles, "VP_PRODUCER");

            newStatus = switch (request.getAction()) {
                case APPROVE -> PublicityApplicationStatus.VP_APPROVED;
                case REJECT -> {
                    requireRemarks(request);
                    yield PublicityApplicationStatus.VP_REJECTED;
                }
                case REMARK -> {
                    requireRemarks(request);
                    app.setRemarkedBy("VICE PRESIDENT");
                    yield PublicityApplicationStatus.DRAFT;
                }
                default -> throw new BadRequestException("Invalid action");
            };
        }

        //SECRETARY
        else if (currentStatus == PublicityApplicationStatus.VP_APPROVED) {

            requireRole(UserRoles, "SECRETARY");

            newStatus = switch (request.getAction()) {
                case APPROVE -> PublicityApplicationStatus.SECRETARY_APPROVED;
                case REJECT -> {
                    requireRemarks(request);
                    yield PublicityApplicationStatus.SECRETARY_REJECTED;
                }
                case REMARK -> {
                    requireRemarks(request);
                    app.setRemarkedBy("SECRETARY");
                    yield PublicityApplicationStatus.DRAFT;
                }
                default -> throw new BadRequestException("Invalid action");
            };
        }

        //MANAGER
        else if (currentStatus == PublicityApplicationStatus.SECRETARY_APPROVED) {

            requireRole(UserRoles, "MANAGER");

            newStatus = switch (request.getAction()) {
                case APPROVE -> {
                    app.setAcceptedAt(LocalDate.now());
                    yield PublicityApplicationStatus.FINAL_APPROVED;
                }
                case REJECT -> {
                    requireRemarks(request);
                    yield PublicityApplicationStatus.MANAGER_REJECTED;
                }
                case REMARK -> {
                    requireRemarks(request);
                    app.setRemarkedBy("MANAGER");
                    yield PublicityApplicationStatus.DRAFT;
                }
                default -> throw new BadRequestException("Invalid action");
            };
        }

        else {
            throw new BadRequestException( "Application not in actionable state");
        }

        if (request.getRemark() != null && !request.getRemark().isBlank()) {
            app.setRemark(request.getRemark());
        }

        app.setStatus(newStatus);

        publicityClearanceRepository.save(app);

    }

    public PublicityClearanceApplication getApplicationDetailsById(Long applicationId, CustomUserDetails user) {

        PublicityClearanceApplication application = publicityClearanceRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        Set<String> roles = user.getRoles();

        boolean isOnlyUser = roles.contains("USER") && roles.contains("PRODUCER") && roles.size() == 2;

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
}
