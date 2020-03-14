package com.zf.dao;

import com.zf.pojo.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ResumeDao extends JpaRepository<Resume,Long> , JpaSpecificationExecutor<Resume> {

}
