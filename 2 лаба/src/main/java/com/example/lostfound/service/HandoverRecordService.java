package com.example.lostfound.service;

import com.example.lostfound.model.HandoverRecord;
import com.example.lostfound.repository.HandoverRecordRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HandoverRecordService {
    private final HandoverRecordRepository repository;

    public HandoverRecordService(HandoverRecordRepository repository) {
        this.repository = repository;
    }

    public List<HandoverRecord> getAll() { return repository.findAll(); }
    public HandoverRecord getById(Long id) { return repository.findById(id).orElse(null); }
    
    public HandoverRecord create(HandoverRecord record) {
        record.setHandoverDate(LocalDateTime.now()); 
        return repository.save(record);
    }
    
    public HandoverRecord update(Long id, HandoverRecord record) {
        record.setId(id);
        // При обновлении тоже не даем менять дату
        record.setHandoverDate(LocalDateTime.now());
        return repository.save(record);
    }
    
    public void delete(Long id) { repository.deleteById(id); }
}