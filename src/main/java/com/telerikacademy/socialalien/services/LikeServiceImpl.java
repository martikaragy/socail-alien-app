package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.models.Like;
import com.telerikacademy.socialalien.models.Post;
import com.telerikacademy.socialalien.models.User;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import com.telerikacademy.socialalien.repositories.contracts.LikeRepository;
import com.telerikacademy.socialalien.services.contracts.LikeService;
import com.telerikacademy.socialalien.services.contracts.PostService;
import com.telerikacademy.socialalien.services.contracts.UserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.List;

@Service
public class LikeServiceImpl implements LikeService {

    private LikeRepository likeRepository;
    private PostService postService;
    private UserService userService;

    public LikeServiceImpl(LikeRepository likeRepository, PostService postService, UserService userService) {
        this.likeRepository = likeRepository;
        this.postService = postService;
        this.userService = userService;
    }

    @Override
    public List<Like> getAll(){
        return likeRepository.findAll();

    }

    @Override
    public Like findById(int id) {
       return likeRepository.findById(id).get();
    }

    @Override
    public List<Like> findAllByUser(User user) {
        return likeRepository.findAllByUser(user);
    }

    @Transactional
    @Override
    public void update(Like like) {

        Like newLike = findById(like.getId());

        newLike.setId(like.getId());
        if (like.getPost() != null)
            newLike.setPost(like.getPost());
        if (like.getUser() != null)
            newLike.setUser(like.getUser());
        if (like.getDeletedStatus() != null)
            newLike.setDeletedStatus(like.getDeletedStatus());
        if (like.getDateDeleted() != null)
            newLike.setDateDeleted(like.getDateDeleted());
        likeRepository.save(newLike);


    }

    @Transactional
    @Override
    public void create(Like like) {

        Post post = postService.findById(like.getPost().getId());

        if (!checkIfUserHasLikedPost(like.getUser(), like.getPost())) {
            likeRepository.save(like);
            post.getLikes().add(like);
            postService.update(post);

        } else {
            post.getLikes().remove(like);
            postService.update(post);
            Like newLike = likeRepository.findByUserAndPost(like.getUser(), like.getPost());
            newLike.setDeletedStatus(DeletedStatusType.DELETED);
            newLike.setDateDeleted(new Timestamp(System.currentTimeMillis()));
            update(newLike);
        }

    }

    @Override
    public void createLikeOnPost(String userName, int postId) {
        User user = userService.getUserByUsername(userName).get();
        Post post = postService.findById(postId);

        Like newLike = new Like();
        newLike.setPost(post);
        newLike.setUser(user);
        newLike.setDeletedStatus(DeletedStatusType.ACTIVE);

        create(newLike);

    }
    @Override
    public List<Like> findAllByPost(Post post)
    {
        return likeRepository.findAllByPost(post);
    }

    @Override
    public void delete(Like like) {
        try {
            checkIfLiketExist(like.getId());
            like.setDeletedStatus(DeletedStatusType.DELETED);
            like.setDateDeleted(new Timestamp(System.currentTimeMillis()));
            likeRepository.save(like);

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("There is no such Like");
        }
    }



    public void checkIfLiketExist(int id) throws FileNotFoundException {
        Like tmpLike = likeRepository.findById(id).get();
        if (tmpLike.getDeletedStatus().equals(DeletedStatusType.DELETED))
            throw new FileNotFoundException("There no such Like!");
    }

    public boolean checkIfUserHasLikedPost(User user, Post post) {
        if (likeRepository.findByUserAndPost(user, post) == null) {
            return false;
        } else
            return true;
    }
}
