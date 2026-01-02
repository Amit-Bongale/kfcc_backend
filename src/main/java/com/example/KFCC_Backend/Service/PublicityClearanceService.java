package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.DTO.PublicityClearance.PublicityClearanceRequestDTO;
import com.example.KFCC_Backend.DTO.PublicityClearance.TitleRegistrationPublicityDTO;
import com.example.KFCC_Backend.DTO.PublicityClearance.TitleWithPublicityStatusDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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


    public List<TitleWithPublicityStatusDTO> getEligibleTitlesForClearance(
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

                    return new TitleWithPublicityStatusDTO(titleDto, status);

                })
                .toList();
    }


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




}
