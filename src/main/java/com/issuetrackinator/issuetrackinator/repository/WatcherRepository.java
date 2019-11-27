package com.issuetrackinator.issuetrackinator.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.issuetrackinator.issuetrackinator.model.Watcher;

@Repository
@Transactional
public interface WatcherRepository extends JpaRepository<Watcher, Long>
{
  
}