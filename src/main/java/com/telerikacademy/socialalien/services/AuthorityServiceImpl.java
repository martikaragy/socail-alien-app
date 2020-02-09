package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.DuplicateEntityException;
import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.exceptions.InvalidInputException;
import com.telerikacademy.socialalien.exceptions.OperationNotAllowedException;
import com.telerikacademy.socialalien.models.Authority;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import com.telerikacademy.socialalien.repositories.contracts.AuthorityRepository;
import com.telerikacademy.socialalien.services.contracts.AuthorityService;
import com.telerikacademy.socialalien.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    private AuthorityRepository authorityRepository;
    private UserService userService;

    @Autowired
    public AuthorityServiceImpl(AuthorityRepository authorityRepository, @Lazy UserService userService) {
        this.authorityRepository = authorityRepository;
        this.userService = userService;
    }

    @Override
    public List<Authority> getByUser(User user){
        return authorityRepository.findByUser(user);
    }

    @Override
    public Authority createAuthority(Authority authorityDto){
        if(authorityDto == null){
            throw new InvalidInputException("Provided authority object cannot be empty.");
        }
        String username = authorityDto.getUsername();
        if(username == null || username.length() == 0){
            throw new InvalidInputException("Username can't be empty.");
        }
        String authority = authorityDto.getAuthority();
        if(authority == null || authority.length()==0){
            throw new InvalidInputException("Authority value cannot be empty. Valid values: ROLE_USER, ROLE_ADMIN");
        }
        userService.getUserByUsername(username).orElseThrow(() -> new EntityNotFoundException(String.format("User with username %s does not exist.", username)));
        Optional<Authority> authorityOptional = authorityRepository.findByUsernameAndAuthority(username, authority);
        if(authorityOptional.isPresent()){
            throw new DuplicateEntityException(String.format("Authority with username %s and authority %s already exists.", username, authority));
        }
        authorityDto.setUsername(username);
        if(!(authority.equals("ROLE_USER")||(authority.equals("ROLE_ADMIN")))){
            throw new InvalidInputException("Invalid authority value. Valid values: ROLE_USER, ROLE_ADMIN");
        }
        authorityDto.setAuthority(authority);
        return authorityRepository.save(authorityDto);
    }

    @Override
    public Authority updateAuthority(int authorityId, Authority authorityDto){
        Optional<Authority> authorityOptional = authorityRepository.findById(authorityId);
        Authority authorityDao = authorityOptional.orElseThrow(() -> new EntityNotFoundException(String.format("Authority with id %d does not exists.", authorityId)));
        if (!authorityDao.getAuthority().equals("ROLE_USER")) {
            throw new OperationNotAllowedException("You can only update authority of type USER to authority of type ADMIN.");
        }
        if(authorityDto == null){
            throw new InvalidInputException("Provided authority object cannot be empty.");
        }
        String authority = authorityDto.getAuthority();
        if(authority != null && authority.length() != 0){
            if (!authority.equals("ROLE_ADMIN")) {
                throw new OperationNotAllowedException(String.format("You cannot update user authority to %s.", authority));
            }
            authorityDao.setAuthority(authority);
        }
        return authorityRepository.save(authorityDao);
    }

    @Override
    public void deleteAuthority(int id){
        Optional<Authority> authorityOptional = authorityRepository.findById(id);
        Authority authorityDao = authorityOptional.orElseThrow(() -> new EntityNotFoundException(String.format("Authority with id %d doesn't exist", id)));
        authorityDao.setDeletedStatus(DeletedStatusType.DELETED);
        authorityDao.setDateDeleted(Timestamp.from(Instant.now()));
        authorityRepository.save(authorityDao);
    }

}
