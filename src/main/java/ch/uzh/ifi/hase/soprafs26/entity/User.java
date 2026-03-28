package ch.uzh.ifi.hase.soprafs26.entity;

import java.io.Serializable;
import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "users") //db table called users
public class User implements Serializable { //allows to b able to b conerted to byte stream

	private static final long serialVersionUID = 1L; //stamp number for serializing and deserializing

	@Id
	@GeneratedValue //primary key that gets auto assigned
	private Long id;

	@Column(nullable = false, unique = true) //field that is required to not b null and unqiue
	private String username;

	@Column(nullable = false)
	private String password; 

	@Column(nullable = false, unique = true)
	private String token;

	@Column(nullable = false)
	private UserStatus status;

	@Column(nullable = false)
	private LocalDate creationDate;

	public Long getId() {                 //need these function as the fields are private so not anyone can just modify directly
		return id;
	}											

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	public LocalDate getCreationDate(){
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}
}