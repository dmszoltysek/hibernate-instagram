package pl.edu.agh.mwo.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class Main {

	Session session;

	public static void main(String[] args) {
		Main main = new Main();
		// tu wstaw kod aplikacji
		System.out.println("OK");

		main.addNewUser("dmszoltysek");
		main.addNewAlbum(1, "Zdjęcia z wakacji", "Lato 2021");
		main.addNewPhoto(1, "Kraków - Rynek");
		main.addNewPhoto(1, "Gdańsk - pomnik Neptuna");


		main.addNewUser("Ann123");
		main.addNewAlbum(2, "Turcja 2021", "Zdjęcia z wakacji w Turcji");
		main.addNewPhoto(2, "Didim");
		main.addNewPhoto(2, "abc");

		main.addNewUser("Jacek");
		main.addNewAlbum(3, "Zdjęcia Jacka", "Różne");
		main.addNewPhoto(3, "Zdjęcie 1");
		main.addNewPhoto(3, "Zdjęcie 2");

		main.addNewUser("Franek");
		main.addNewAlbum(4, "Zdjęcia Franka", "`Zdjęcia");
		main.addNewPhoto(4, "Zdjęcie 8");
		main.addNewPhoto(4, "Nowe zdjęcie");
		main.addNewAlbum(4, "Inne", "Różne zdjęcia");
		main.addNewPhoto(5, "Zdjęcie 3");
		main.addNewPhoto(5, "Zdjęcie 4");

		main.like(1, 3);
		main.like(3, 1);
		main.like(2, 1);
		main.like(3, 2);
		main.like(2, 3);
		main.like(4, 1);
		main.like(4, 5);
		main.like(4, 6);
		main.like(3, 2);
		main.like(1, 6);
		main.like(1, 5);
		main.like(2, 6);
		main.like(4, 1);
		main.like(3, 7);
		main.like(4, 4);
		main.like(2, 7);
		main.like(2, 8);
		main.like(2, 9);
		main.like(4, 8);
		main.like(3, 5);
		main.like(3, 6);
		main.printUsers();

		System.out.println("");
		System.out.println("");

		main.unlike(4, 8);
		main.unlike(2, 8);
		main.deletePhoto(5);
		main.deleteAlbum(3);
		main.deleteUser(1);

		main.printUsers();

		Transaction transaction = main.session.beginTransaction();
		transaction.commit();

		main.close();
	}

	public Main() {
		session = HibernateUtil.getSessionFactory().openSession();
	}

	public void close() {
		session.close();
		HibernateUtil.shutdown();
	}

	public void addNewUser(String name) {
		User user = new User();
		user.setUsername(name);
		Transaction transaction = session.beginTransaction();
		session.save(user);
		transaction.commit();
	}

	public void addNewAlbum(long userID, String name, String description) {
		org.hibernate.query.Query<User> query = session.createQuery("from User where id =: id", User.class);
		query.setParameter("id", userID);
		User user = query.uniqueResult();


		Album album = new Album();
		album.setName(name);
		album.setDescription(description);
		user.addAlbum(album);
		album.setUser(user);
		Transaction transaction = session.beginTransaction();
		session.save(user);
		session.save(album);
		transaction.commit();
	}

	public void addNewPhoto(long albumID, String name) {
		org.hibernate.query.Query<Album> query = session.createQuery("from Album where id =: id", Album.class);
		query.setParameter("id", albumID);
		Album album = query.uniqueResult();

		Photo photo = new Photo();
		photo.setName(name);
		photo.setAlbum(album);
		album.addPhoto(photo);
		Transaction transaction = session.beginTransaction();
		session.save(photo);
		session.save(album);
		transaction.commit();
	}

	public void like(long userID, long photoID) {
		org.hibernate.query.Query<User> query = session.createQuery("from User where id =: id", User.class);
		query.setParameter("id", userID);
		User user = query.uniqueResult();

		org.hibernate.query.Query<Photo> query1 = session.createQuery("from Photo where id =: id", Photo.class);
		query1.setParameter("id", photoID);
		Photo photo = query1.uniqueResult();

		photo.addLike(user);
		user.addLikedPhoto(photo);
		Transaction transaction = session.beginTransaction();
		session.save(user);
		session.save(photo);
		transaction.commit();
	}

	public void unlike(long userID, long photoID) {
		org.hibernate.query.Query<User> query = session.createQuery("from User where id =: id", User.class);
		query.setParameter("id", userID);
		User user = query.uniqueResult();

		org.hibernate.query.Query<Photo> query1 = session.createQuery("from Photo where id =: id", Photo.class);
		query1.setParameter("id", photoID);
		Photo photo = query1.uniqueResult();

		Transaction transaction = session.beginTransaction();
		photo.removeLike(user);
		user.removeLikedPhoto(photo);
		session.save(user);
		session.save(photo);
		transaction.commit();
	}

	public void deletePhoto(long photoID) {
		org.hibernate.query.Query<Photo> query = session.createQuery("from Photo where id =: id", Photo.class);
		query.setParameter("id", photoID);
		Photo photo = query.uniqueResult();

		Transaction deletePhotoTransaction = session.beginTransaction();
		for (User user : photo.getLikes()) {
			user.removeLikedPhoto(photo);
			session.save(user);
		}
		Album album = photo.getAlbum();
		album.removePhoto(photo);
		session.save(album);
		session.delete(photo);
		deletePhotoTransaction.commit();
	}

	public void deleteAlbum(long albumID) {
		org.hibernate.query.Query<Album> query = session.createQuery("from Album where id =: id", Album.class);
		query.setParameter("id", albumID);
		Album album = query.uniqueResult();

		Transaction deleteAlbumTransaction = session.beginTransaction();
		for (Photo photo : album.getPhotos()
		) {
			for (User user : photo.getLikes()) {
				user.removeLikedPhoto(photo);
				session.save(user);
			}
			session.delete(photo);
		}

		User user = album.getUser();
		user.removeAlbum(album);
		session.delete(album);
		deleteAlbumTransaction.commit();
	}

	public void deleteUser(long userID) {
		org.hibernate.query.Query<User> query = session.createQuery("from User where id =: id", User.class);
		query.setParameter("id", userID);
		User user = query.uniqueResult();

		Transaction deleteUserTransaction = session.beginTransaction();

		for (Photo photo : user.getLikedPhotos()) {
			photo.removeLike(user);
			session.save(photo);
		}

		for (Album album : user.getAlbums()) {
			for (Photo photo : album.getPhotos()
			) {
				for (User user1 : photo.getLikes()) {
					user1.removeLikedPhoto(photo);
					session.save(user);
				}
				session.delete(album);
			}
		}
		session.delete(user);
		deleteUserTransaction.commit();

	}


	private void printUsers() {
		Query<User> query = session.createQuery("from User", User.class);
		List<User> users = query.list();

		System.out.println("### Users:");
		for (User user : users) {
			System.out.println(user);
			System.out.println("### Albums:");
			for (Album album : user.getAlbums()) {
				System.out.println("   " + album);
				System.out.println("### Photos:");
				for (Photo photo : album.getPhotos()) {
					System.out.println("    " + photo);
				}
			}
			System.out.println("### Liked photos:");
			for (Photo photo : user.getLikedPhotos()) {
				System.out.println("    " + photo);
			}
		}
	}
}