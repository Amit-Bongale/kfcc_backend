package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.DTO.IdCard.IdCardResponseDTO;
import com.example.KFCC_Backend.Entity.IdCardRequests;
import com.example.KFCC_Backend.Entity.Membership.MembershipApplication;
import com.example.KFCC_Backend.Entity.Users;
import com.example.KFCC_Backend.ExceptionHandlers.BadRequestException;
import com.example.KFCC_Backend.ExceptionHandlers.ResourceNotFoundException;
import com.example.KFCC_Backend.Repository.IdCard.IdCardRequestRepository;
import com.example.KFCC_Backend.Repository.Membership.MembershipRepository;
import com.example.KFCC_Backend.Repository.Users.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Utility.FileStorageUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.AccessException;
import java.time.LocalDate;
import java.util.List;


@Service
public class IdCardRequestService {

    @Autowired
    private IdCardRequestRepository idCardRequestRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private FileStorageUtil fileStorageUtil;


    public IdCardResponseDTO mapToDto(IdCardRequests request) {

        Users user = request.getUser();
        MembershipApplication app = request.getApplication();

        return new IdCardResponseDTO(
                // USER
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getBloodGroup(),
                user.getDob(),

                // MEMBERSHIP
                app.getApplicantMembershipCategory(),
                app.getMembershipId(),
                app.getApplicantFirmName(),
                app.getApplicantAddressLine1(),
                app.getApplicantAddressLine2(),
                app.getMembershipExpiryDate(),

                // ID CARD
                request.getId(),
                request.getApplicantImage(),
                request.getStatus().name(),
                request.getIssuedAt(),
                request.getSubmittedAt()
        );
    }


    //apply for Id
    @Transactional
    public IdCardResponseDTO requestIdCard(Long membershipId, CustomUserDetails ReqUser , MultipartFile image) throws IOException {

        Users user = usersRepository.findById(ReqUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MembershipApplication application = membershipRepository.findByApplicationId(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (application.getMembershipExpiryDate() == null ||
                application.getMembershipExpiryDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Membership is expired");
        }

        if(!application.getUser().getId().equals(user.getId()) ){
            throw new AccessException("User does not Match");
        }

        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Applicant image is required");
        }

        System.out.println("Saving applicant image...");
        String path = fileStorageUtil.saveFile(
                "IdCards",
                user.getId().toString(),
                image
        );

        IdCardRequests request = new IdCardRequests();
        request.setUser(user);
        request.setApplication(application);
        request.setSubmittedAt(LocalDate.now());
        request.setStatus(IdCardRequests.IdCardStatus.REQUESTED);
        request.setApplicantImage(path);

        return mapToDto( idCardRequestRepository.save(request));
    }

    //show all pending requests
    public List<IdCardResponseDTO> getAllRequests() {
        List<IdCardRequests> requests =
                idCardRequestRepository.findByStatus(
                        IdCardRequests.IdCardStatus.REQUESTED
                );

        return requests.stream()
                .map(this::mapToDto)
                .toList();
    }

    //change status to issued
    public void cardIssued( Long appId){

        IdCardRequests requests = idCardRequestRepository.findById(appId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        requests.setStatus(IdCardRequests.IdCardStatus.ISSUED);
        requests.setIssuedAt(LocalDate.now());

        idCardRequestRepository.save(requests);
    }

}
