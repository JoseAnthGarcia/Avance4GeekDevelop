package com.example.demo.service;

import com.example.demo.dtos.ExtraDTO;
import com.example.demo.entities.Extra;
import org.springframework.data.domain.Page;

public interface ExtraService {

    Page<Extra> findPaginated(int pageNo, int pageSize);
}
