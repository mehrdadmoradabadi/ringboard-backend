package com.RingBoard.wallboard.ugroup;

import com.RingBoard.wallboard.ugroup.dto.GroupDto;
import com.RingBoard.wallboard.user.User;
import com.RingBoard.wallboard.user.UserService;
import com.RingBoard.wallboard.utils.ResourceNotFoundException;
import com.RingBoard.wallboard.utils.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserService userService;
    private GroupDto.GroupResponse mapToResponse(Group groups) {
        GroupDto.GroupResponse groupDto = new GroupDto.GroupResponse();

        groupDto.setId(groups.getId());
        groupDto.setName(groups.getName());
        groupDto.setUsers(groups.getUsers().stream().map(User::getUsername).collect(Collectors.toSet()));
        return groupDto;
    }
    private Group mapToEntity(GroupDto.CreateGroupResponse request) {
        Set<User> users = request.getUsers().stream().map(userId -> userService.findById(Integer.parseInt(userId))).collect(Collectors.toSet());
        Group group = new Group();
        group.setName(request.getName());
        group.setUsers(users);

        return group;
    }
    @Transactional
    public GroupDto.GroupResponse save(GroupDto.CreateGroupResponse groupRequest) {
        Group group = mapToEntity(groupRequest);
        group.setCreatedAt(ZonedDateTime.now());
        group.setUpdatedAt(ZonedDateTime.now());
        group.setUsers(group.getUsers());
        group.setName(groupRequest.getName());
        return mapToResponse(groupRepository.save(group));
    }

    public GroupDto.GroupResponse findByName(String name) {
        Group group = groupRepository.findByName(name);

        if (group == null) {
            throw new ResourceNotFoundException("Group not found with name: " + name);
        }
        return mapToResponse(groupRepository.findByName(name));
    }

    @Transactional
    public void deleteByName(String name) {
        Group group = groupRepository.findByName(name);

        if (group == null) {
            throw new ResourceNotFoundException("Group not found with name: " + name);
        }
        groupRepository.deleteByName(name);
    }

    @Transactional
    public void delete(Group Group) {
        Group group = groupRepository.findByName(Group.getName());

        if (group == null) {
            throw new ResourceNotFoundException("Group not found with name: " + Group);
        }
        groupRepository.delete(Group);
    }

    public GroupDto.GroupResponse update(GroupDto.UpdateGroupResponse group) {
        Group existingGroup = groupRepository.findById(group.getId()).orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + group.getId()));
        Set<User> users = group.getUsers().stream().map(userId -> userService.findById(Integer.parseInt(userId))).collect(Collectors.toSet());
        if (group.getName() != null) existingGroup.setName(group.getName());
        if (group.getUsers() != null) existingGroup.setUsers(users);
        existingGroup.setUpdatedAt(ZonedDateTime.now());
        return mapToResponse(groupRepository.save(existingGroup));
    }


    public SearchResponse<List<GroupDto.GroupResponse>> findAll(int page,String search ,String sortBy,String sortDirection) {
        int pageSize =10;
        List<Group> Groups;
        List<GroupDto.GroupResponse> groupDtosList;
        long totalGroups;
        if (search != null && !search.isEmpty()) {
            Groups = groupRepository.findByNameContainingIgnoreCase(search);
            totalGroups= Groups.size();
        } else {
            Groups = groupRepository.findAll();
            totalGroups = groupRepository.count();
        }
        if(sortBy!=null){
            Comparator<Group> comparator = switch (sortBy.toLowerCase()){
                case "name" -> Comparator.comparing(Group::getName);
                case "created_at" -> Comparator.comparing(Group::getCreatedAt);
                case "updated_at" -> Comparator.comparing(Group::getUpdatedAt);
                default -> throw new IllegalArgumentException("Invalid sortBy parameter: " + sortBy);
            };
            if(sortDirection.equalsIgnoreCase("desc")){
                Groups.sort(comparator.reversed());
            }
            Groups.sort(comparator);
        }

        long totalPages = (totalGroups + pageSize - 1) / pageSize;
        if(page!=0){
            List<Group> pagedGroups = Groups.stream()
                    .skip((long) (page - 1) * pageSize)
                    .limit(pageSize)
                    .toList();
            groupDtosList= pagedGroups.stream().map(this::mapToResponse).collect(Collectors.toList());}
        else {
            groupDtosList= Groups.stream().map(this::mapToResponse).collect(Collectors.toList());
        }
        return new SearchResponse<>(page, totalPages, groupDtosList);
    }


    public GroupDto.GroupResponse findById(Long id) {
        return groupRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));
    }

}
