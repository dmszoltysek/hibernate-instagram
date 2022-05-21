package pl.edu.agh.mwo.hibernate;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String username;

    @Column
    private LocalDate joinDate = LocalDate.now();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Set<Album> albums = new HashSet<>();

    @ManyToMany(mappedBy = "likes", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Photo> likedPhotos = new HashSet<>();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getJoinDate() { return joinDate; }

    public Set<Photo> getLikedPhotos() {
        return likedPhotos;
    }

    public void addLikedPhoto(Photo photo) {
        likedPhotos.add(photo);
    }

    public void removeLikedPhoto(Photo photo) {
        likedPhotos.remove(photo);
    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void addAlbum(Album album) { albums.add(album); }

    public void removeAlbum(Album album) {
        albums.remove(album);
    }

    @Override
    public String toString() {
        return "User: " + username + " join date: " + joinDate;
    }
}
