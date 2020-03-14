package com.zf.service.impl;

import com.zf.dao.ResumeDao;
import com.zf.pojo.Resume;
import com.zf.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResumeServiceImpl implements ResumeService {


    @Autowired
    private ResumeDao resumeDao;


    @Override
    public List<Resume> queryResumeList() {
        List<Resume> resumes = resumeDao.findAll();
        return resumes;
    }

    @Override
    public Resume updateResume(Resume updateResume) {
        Resume resume = resumeDao.save(updateResume);
        return resume;
    }

    @Override
    public void deleteResume(String id) {
        resumeDao.deleteById(Long.parseLong(id));
    }

    @Override
    public Resume findResume(String id) {
        Optional<Resume> resumeOptional = resumeDao.findById(Long.parseLong(id));
        return resumeOptional.get();
    }

    @Override
    public Resume addResume(Resume re) {
        return resumeDao.save(re);
    }

}
