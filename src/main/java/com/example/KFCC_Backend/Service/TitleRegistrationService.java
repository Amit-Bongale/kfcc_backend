package com.example.KFCC_Backend.Service;

import com.example.KFCC_Backend.Enum.MembershipStatus;
import com.example.KFCC_Backend.Enum.TitleApplicationStatus;
import com.example.KFCC_Backend.Repository.MembershipRepository;
import com.example.KFCC_Backend.Repository.TitleRegistrationRepository;
import com.example.KFCC_Backend.Repository.UsersRepository;
import com.example.KFCC_Backend.Service.CustomUserDetails.CustomUserDetails;
import com.example.KFCC_Backend.entity.Title.TitleRegistration;
import com.example.KFCC_Backend.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TitleRegistrationService {

    @Autowired
    private TitleRegistrationRepository titleRegistrationRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    public TitleRegistration submitApplication(TitleRegistration request , CustomUserDetails userDetails){
        Long producerId = userDetails.getUserId();

        Users producer  = usersRepository.findById(producerId).orElseThrow(() -> new RuntimeException("User Not Found"));

        // check if producer has valid membership
        boolean hasValidMembership = membershipRepository.hasValidMembership(producerId , MembershipStatus.FINAL_APPROVED, LocalDate.now());

        if (!hasValidMembership) {
            throw new IllegalStateException(
                    "Valid membership required to apply for title registration"
            );
        }

        if (titleRegistrationRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new IllegalStateException("Title already registered or in process");
        }

        TitleRegistration app = new TitleRegistration();
        app.setProducer(producer);
        app.setTitle(request.getTitle().trim());
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

        return titleRegistrationRepository.save(app);

    }

}

