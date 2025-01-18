package com.wallboard.wallboard.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    public Group findByName(String name) {
        return groupRepository.findByName(name);
    }

    public void deleteByName(String name) {
        groupRepository.deleteByName(name);
    }

    public void delete(Group group) {
        groupRepository.delete(group);
    }

    public Group update(Group group) {
        return groupRepository.save(group);
    }

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public Group findById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

}
