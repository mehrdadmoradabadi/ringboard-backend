package com.wallboard.wallboard.group;

import com.wallboard.wallboard.dto.GroupDto;
import com.wallboard.wallboard.user.User;
import com.wallboard.wallboard.utils.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public GroupDto mapToDto(Group groups) {
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


    public Group findById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

}
