package com.telerikacademy.socialalien.services;

import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.models.*;
import com.telerikacademy.socialalien.models.enums.ConnectionType;
import com.telerikacademy.socialalien.models.enums.DeletedStatusType;
import com.telerikacademy.socialalien.models.enums.PrivacyType;
import com.telerikacademy.socialalien.repositories.contracts.PostRepository;
import com.telerikacademy.socialalien.services.contracts.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private ConnectionService connectionService;
    private UserService userService;
    private CommentService commentService;
    private LikeService likeService;

    public PostServiceImpl(PostRepository postRepository,@Lazy UserService userService, @Lazy ConnectionService connectionService ,@Lazy CommentService commentService, @Lazy LikeService likeService) {
        this.postRepository = postRepository;

        this.connectionService = connectionService;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
    }




    @Override
    public List<Post> getAll(){
        return postRepository.findAll();

    }

    @Override
    public List<Post> getAllByUser(User user){
        return postRepository.findAllByUser(user);
    }

    @Override
    public Post findById(int id) {

        return postRepository.findById(id).get();
    }


    @Transactional
    @Override
    public void update(Post post) {
        Post postDao = postRepository.findById(post.getId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Post with id %d does not exist.", post.getId())));
        if (post.getUser() != null) {
            postDao.setUser(post.getUser());
        }
        if (post.getValue() != null) {
            postDao.setValue(post.getValue());
        }
        if (post.getComments() != null) {
            postDao.setComments(post.getComments());
        }
        if (post.getLikes() != null) {
            postDao.setLikes(post.getLikes());
        }
        if (post.getDeletedStatus() != null) {
            postDao.setDeletedStatus(post.getDeletedStatus());
        }
        if (post.getDateDeleted() != null) {
            postDao.setDateDeleted(post.getDateDeleted());
        }
        postRepository.save(postDao);
    }


    @Transactional
    @Override
    public void create(Post post) {
        if (post.getPrivacyType() == null || !post.getPrivacyType().toString().equals("PRIVATE")){
           post.setPrivacyType(PrivacyType.PUBLIC);
        }
        if(post.getPrivacyType().toString().equals("PRIVATE"))
            post.setPrivacyType(PrivacyType.PRIVATE);

        post.setDeletedStatus(DeletedStatusType.ACTIVE);
        Post postDao = postRepository.save(post);
        User user = userService.getUserById(
                post.getUser().getId()).orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d does not exist.", post.getUser().getId())));
        user.getPosts().add(postDao);
        userService.updateUser(user);
    }

    @Override
    public List<Post> getPostsByPrivacyType(PrivacyType privacyType) {
        return postRepository.getAllByPrivacyType(privacyType);
    }

    @Override
    public List<Post> findAllByUserId(Integer id) {
        User tmpUser = userService.getUserById(id).get();
         return  postRepository.findAllByUser(tmpUser);
 }
    @Transactional
    @Override
    public void delete(Post post) {
        Post postDao = postRepository.findById(post.getId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Post with id %d does not exist.", post.getId())));
        List<Comment> comments=commentService.findAllByPost(postDao);
        List<Like> likes = likeService.findAllByPost(post);
        for (Like like:likes) {
            like.setDeletedStatus(DeletedStatusType.DELETED);
            like.setDateDeleted(Timestamp.from(Instant.now()));
            likeService.update(like);
        }
        for (Comment c:comments) {
            c.setDeletedStatus(DeletedStatusType.DELETED);
            c.setDateDeleted(Timestamp.from(Instant.now()));
            commentService.update(c);
        }

        postDao.setDeletedStatus(DeletedStatusType.DELETED);
        postDao.setDateDeleted(Timestamp.from(Instant.now()));
        update(postDao);

    }

    @Override
    public  List<Post> createNewsFeed(String principalName){

        List<Post> posts =getPostsByPrivacyType(PrivacyType.PUBLIC);
        if(principalName.equals("anonymousUser"))
            return  posts;
        else
        {
            User user = userService.getUserByUsername(principalName).get();
            posts.addAll(getAllByUser(user)) ;

               if(connectionService.getAllConnectionsOfUserByConnectionStatus(user.getId(), ConnectionType.APPROVED)==null)
                   return posts;
                List<Connection> connections = connectionService.getAllConnectionsOfUserByConnectionStatus(user.getId(), ConnectionType.APPROVED);

                for(Connection connection: connections)
            {

              posts.addAll(postRepository.findAllByUser(connection.getReceiverUser()));
              posts.addAll(postRepository.findAllByUser(connection.getSenderUser()));
            }
        }
        posts=posts.stream().distinct().collect(Collectors.toList());

        List<Post> sortedList = posts.stream()
                                     .sorted(Comparator.comparing(Post::getDateCreated))
                                     .collect(Collectors.toList());
        Collections.reverse(sortedList);
        return sortedList;
    }

    @Override
    public List<Post> createUserFeed(String principleName, int userId) {
        User user= userService.getUserById(userId).get();
        List<Post> posts = postRepository.findAllByUserAndPrivacyType(user,PrivacyType.PUBLIC);
        if(principleName.equals(user.getUsername())) {
            posts = postRepository.findAllByUser(user);
            Collections.reverse(posts);
            return posts;
        }
        if(principleName.equals("userAnonymous")) {
            Collections.reverse(posts);
            return posts;
        }
        else
        {
            User user1 =userService.getUserByUsername(principleName).get();
            if(!connectionService.getConnectionByUserCouple(user1,user).getConnectionStatus().equals(ConnectionType.APPROVED))
            {
                Collections.reverse(posts);
                return posts;
            }
            else {
                posts=postRepository.findAllByUser(user);
                Collections.reverse(posts);
                return posts;
            }
        }
    }


    public void checkIfPostExist(int id) throws FileNotFoundException {
        Post tmpPost = postRepository.findById(id).get();
        if (tmpPost.getDeletedStatus().equals(DeletedStatusType.DELETED))
            throw new FileNotFoundException("There no such Post!");
    }


}
