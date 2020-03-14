package com.zf.service;

import com.zf.pojo.Resume;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ResumeService {


    List<Resume> queryResumeList();

    Resume updateResume(Resume resume);

    void deleteResume(String id);

    Resume findResume(String id);

    Resume addResume(Resume re);
}
