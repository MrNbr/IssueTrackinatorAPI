package com.issuetrackinator.issuetrackinator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.issuetrackinator.issuetrackinator.model.UploadedFile;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long>
{

}
