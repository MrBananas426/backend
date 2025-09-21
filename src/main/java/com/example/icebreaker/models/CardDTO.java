package com.example.icebreaker.models;

public class CardDTO {
  private String firstName;
  private String lastName;
  private String vibeIndicator;
  private String[] talkAbout;

  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getVibeIndicator() { return vibeIndicator; }
  public void setVibeIndicator(String vibeIndicator) { this.vibeIndicator = vibeIndicator; }

  public String[] getTalkAbout() { return talkAbout; }
  public void setTalkAbout(String[] talkAbout) { this.talkAbout = talkAbout; }
}

