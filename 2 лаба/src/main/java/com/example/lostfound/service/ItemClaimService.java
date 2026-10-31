package com.example.lostfound.service;

import com.example.lostfound.model.ItemClaim;
import com.example.lostfound.repository.ItemClaimRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemClaimService {
    private final ItemClaimRepository repository;

    public ItemClaimService(ItemClaimRepository repository) {
        this.repository = repository;
    }

    public List<ItemClaim> getAll() { 
        return repository.findAll(); 
    }
    
    public ItemClaim getById(Long id) { 
        return repository.findById(id).orElse(null); 
    }
    
    public ItemClaim create(ItemClaim claim) { 
        return repository.save(claim); 
    }
    
    public ItemClaim update(Long id, ItemClaim claim) {
        claim.setId(id);
        return repository.save(claim);
    }
    
    public void delete(Long id) { 
        repository.deleteById(id); 
    }
}