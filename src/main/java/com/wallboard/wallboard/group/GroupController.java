package com.wallboard.wallboard.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @RequestMapping("/save")
    public Group save(Group group) {
        return groupService.save(group);
    }

    @RequestMapping("/delete")
    public void delete(Group group) {
        groupService.delete(group);
    }

    @RequestMapping("/deleteByName")
    public void deleteByName(String name) {
        groupService.deleteByName(name);
    }

    @RequestMapping("/findByName")
    public Group findByName(String name) {
        return groupService.findByName(name);
    }

    @RequestMapping("/all")
    public List<Group> findAll() {
        return groupService.findAll();
    }

    @RequestMapping("/update")
    public Group update(Group group) {
        return groupService.update(group);
    }

    @RequestMapping("/findById")
    public Group findById(Long id) {
        return groupService.findById(id);
    }
}
