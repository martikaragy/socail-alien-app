package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.exceptions.InvalidInputException;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.Visibility;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import com.telerikacademy.socialalien.repositories.contracts.VisibilityRepository;
import com.telerikacademy.socialalien.services.contracts.VisibilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class VisibilityServiceImpl implements VisibilityService {
    private VisibilityRepository visibilityRepository;

    @Autowired
    public VisibilityServiceImpl(VisibilityRepository visibilityRepository) {
        this.visibilityRepository = visibilityRepository;
    }

    @Override
    public Visibility getAllPublicVisibility() {
        return new Visibility(true, true, true, true, true, true, true, true, true, true, true);
    }

    @Override
    public Optional<Visibility> getVisibilityForUser(User user) {
        return visibilityRepository.findByUser(user);
    }

    @Override
    public Visibility create(Visibility visibility) {
        return visibilityRepository.save(visibility);
    }

    @Override
    public Visibility update(Visibility visibility) {
        if (visibility.getUser() == null) {
            throw new InvalidInputException("The user property for the visibility object cannot be empty.");
        }
        Visibility visibilityDao = visibilityRepository.findByUser(visibility.getUser()).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id %d does not exist.", visibility.getUser().getId())));
        visibilityDao.setUsername(visibility.isUsername());
        visibilityDao.setFirstName(visibility.isFirstName());
        visibilityDao.setLastName(visibility.isLastName());
        visibilityDao.setEmail(visibility.isEmail());
        visibilityDao.setAddress(visibility.isAddress());
        visibilityDao.setDate(visibility.isDate());
        visibilityDao.setCityOfBirth(visibility.isCityOfBirth());
        visibilityDao.setCityOfResidence(visibility.isCityOfResidence());
        visibilityDao.setJobTitle(visibility.isJobTitle());
        visibilityDao.setEducationLevel(visibility.isEducationLevel());
        visibilityDao.setProfilePhoto(visibility.isProfilePhoto());
        return visibilityRepository.save(visibilityDao);
    }

    @Override
    public void delete(int id) {
        Visibility visibility = visibilityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Visibility with id %d does not exist.", id)));
        visibility.setDeletedStatus(DeletedStatusType.DELETED);
        visibility.setDateDeleted(Timestamp.from(Instant.now()));
        visibilityRepository.save(visibility);
    }

}
