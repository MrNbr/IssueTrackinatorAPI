package com.issuetrackinator.issuetrackinator.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class Watcher
{
  // Watcher/user id
  @Id
  private Long id;
  
  // List of all issues being watched by the user
  @NotNull
  private Set<Long> watchingList = new HashSet<>();
  
  // CONSTRUCTOR
  public Watcher(Long user_id){
    this.id = user_id;
  }
  
  public void addWatcher(Long issue_id){
    this.watchingList.add(issue_id);
  }
  
  public void removeWatcher(Long issue_id){
    this.watchingList.remove(issue_id);
  }
  
  public bool isWatching(Long issue_id){
    return this.watchingList.contains(issue_id);
  }
}