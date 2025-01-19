package com.wallboard.wallboard.group;

import com.wallboard.wallboard.dto.GroupDto;
import com.wallboard.wallboard.user.User;
import com.wallboard.wallboard.utils.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    public GroupDto save(Group group) {
        return mapToDto(groupRepository.save(group));
    }

    public GroupDto findByName(String name) {
        return mapToDto(groupRepository.findByName(name));
    }

    public void deleteByName(String name) {
        groupRepository.deleteByName(name);
    }

    public void delete(Group group) {
        groupRepository.delete(group);
    }

    public GroupDto update(Group group) {
        Group existingGroup = groupRepository.findById(group.getId()).orElse(null);
        assert existingGroup != null;
        existingGroup.setName(group.getName());
        existingGroup.setUsers(group.getUsers());
        existingGroup.setUpdatedAt(ZonedDateTime.now());
        return mapToDto(groupRepository.save(existingGroup));
    }

    private GroupDto mapToDto(Group groups) {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(groups.getId());
        groupDto.setName(groups.getName());
        groupDto.setUsers(groups.getUsers().stream().map(User::getUsername).toList());
        return groupDto;
    }
    public SearchResponse<List<GroupDto>> findAll(int page,String search ,String sortBy,String sortDirection) {
        int pageSize =10;
        List<Group> groups;
        List<GroupDto> groupDtosList;
        long totalGroups;
        if (search != null && !search.isEmpty()) {
            groups = groupRepository.findByNameContainingIgnoreCase(search);
            totalGroups= groups.size();
        } else {
            groups = groupRepository.findAll();
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
                groups.sort(comparator.reversed());
            }
            groups.sort(comparator);
        }

        long totalPages = (totalGroups + pageSize - 1) / pageSize;
        if(page!=0){
            List<Group> pagedGroups = groups.stream()
                    .skip((long) (page - 1) * pageSize)
                    .limit(pageSize)
                    .toList();
            groupDtosList= pagedGroups.stream().map(this::mapToDto).collect(Collectors.toList());}
        else {
            groupDtosList= groups.stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return new SearchResponse<>(page, totalPages, groupDtosList);
    }


    public GroupDto findById(Long id) {
        return mapToDto(Objects.requireNonNull(groupRepository.findById(id).orElse(null)));
    }

}
