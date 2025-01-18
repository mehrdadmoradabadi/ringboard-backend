package com.wallboard.wallboard.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    public Resource create(Resource resource) {
        return resourceRepository.save(resource);
    }

    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    public Resource findById(Long id) {
        return resourceRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        resourceRepository.deleteById(id);
    }

    public Resource update(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Resource findByName(String name) {
        return resourceRepository.findByName(name);
    }

    public List<Resource> findByType(String type) {
        return resourceRepository.findByType(type);
    }

    public List<Resource> findByMetadata(String key, Object value) {
        return resourceRepository.findByMetadata(key, value);
    }

    public void deleteByName(String name) {
        resourceRepository.deleteByName(name);
    }

    public void deleteByPbxId(Long pbxId) {
        resourceRepository.deleteByPbxId(pbxId);
    }

}
