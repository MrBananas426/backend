package com.example.icebreaker.models;

import jakarta.persistence.*;

@Entity
@Table(name = "card")
public class Card {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "visibility")
  private String visibility;

  @Column(name = "vibe_indicator")
  private String vibeIndicator;

  @Column(name = "location_cue")
  private String locationCue;

  private String tone;

  @Column(name = "open_to")
  private String[] openTo;

  @Column(name = "talk_about")
  private String[] talkAbout;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getVisibility() { return visibility; }
  public void setVisibility(String visibility) { this.visibility = visibility; }

  public String getVibeIndicator() { return vibeIndicator; }
  public void setVibeIndicator(String vibeIndicator) { this.vibeIndicator = vibeIndicator; }

  public String getLocationCue() { return locationCue; }
  public void setLocationCue(String locationCue) { this.locationCue = locationCue; }

  public String getTone() { return tone; }
  public void setTone(String tone) { this.tone = tone; }

  public String[] getOpenTo() { return openTo; }
  public void setOpenTo(String[] openTo) { this.openTo = openTo; }

  public String[] getTalkAbout() { return talkAbout; }
  public void setTalkAbout(String[] talkAbout) { this.talkAbout = talkAbout; }
}
