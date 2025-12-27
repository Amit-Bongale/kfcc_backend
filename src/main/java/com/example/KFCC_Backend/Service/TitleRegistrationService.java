package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.Enum.TitleApplicationStatus;
import com.example.KFCC_Backend.ExceptionHandlers.BadRequestException;
import com.example.KFCC_Backend.ExceptionHandlers.ResourceNotFoundException;
import com.example.KFCC_Backend.Repository.MembershipRepository;
import com.example.KFCC_Backend.Repository.TitleRegistrationDocumentsRepository;
import com.example.KFCC_Backend.Repository.TitleRegistrationRepository;
import com.example.KFCC_Backend.Repository.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.Utility.FileStorageUtil;
import com.example.KFCC_Backend.entity.Title.TitleRegistration;
import com.example.KFCC_Backend.entity.Title.TitleRegistrationDocuments;
import com.example.KFCC_Backend.entity.Users;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
            throw new org.apache.coyote.BadRequestException("Title already registered or in process");
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

            // REUSING UTILITY
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
        return titleRegistrationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

}

