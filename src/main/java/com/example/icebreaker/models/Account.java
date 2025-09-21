package com.example.icebreaker.models;

import jakarta.persistence.*;
import java.util.Map;

@Entity
@Table(name = "account")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  private String password;

  private String role;

  @Column(name = "created_at")
  private String createdAt;

  @Column(name = "user_id", unique = true)
  private String userId;

  private String provider;

  private String birthday;

  private String gender;

  // The important part: cascade so a new Card gets saved with the Account
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "card_id", unique = true)
  private Card card;

  // Not stored in DB; used for OAuth login context
  @Transient
  private Map<String, Object> attributes;

  // --- Getters / Setters ---

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }

  public String getCreatedAt() { return createdAt; }
  public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }

  public String getProvider() { return provider; }
  public void setProvider(String provider) { this.provider = provider; }

  public String getBirthday() { return birthday; }
  public void setBirthday(String birthday) { this.birthday = birthday; }

  public String getGender() { return gender; }
  public void setGender(String gender) { this.gender = gender; }

  public Card getCard() { return card; }
  public void setCard(Card card) { this.card = card; }

  public Map<String, Object> getAttributes() { return attributes; }
  public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
}
