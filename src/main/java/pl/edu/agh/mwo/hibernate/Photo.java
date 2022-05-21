package pl.edu.agh.mwo.hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column
    private LocalDateTime uploadDate = LocalDateTime.now();

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "album_id")
    private Album album;

//    cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "likes",
            joinColumns = @JoinColumn(name = "photo_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likes = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Set<User> getLikes() {
        return likes;
    }
    
    public void addLike(User user) {
        likes.add(user);
    }

    public void removeLike(User user) {
        likes.remove(user);
    }

    @Override
    public String toString() {
        return "Photo: " + name + " (upload date: " + uploadDate + ")";
    }


}
