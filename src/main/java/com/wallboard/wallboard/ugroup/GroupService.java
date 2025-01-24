package com.wallboard.wallboard.ugroup;

import com.wallboard.wallboard.dto.GroupDto;
import com.wallboard.wallboard.utils.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wallboard.wallboard.user.User;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    public GroupDto save(UGroup UGroup) {
        return mapToDto(groupRepository.save(UGroup));
    }

    public GroupDto findByName(String name) {
        return mapToDto(groupRepository.findByName(name));
    }

    public void deleteByName(String name) {
        groupRepository.deleteByName(name);
    }

    public void delete(UGroup UGroup) {
        groupRepository.delete(UGroup);
    }

    public GroupDto update(UGroup UGroup) {
        UGroup existingUGroup = groupRepository.findById(UGroup.getId()).orElse(null);
        assert existingUGroup != null;
        existingUGroup.setName(UGroup.getName());
//        existingUGroup.setUsers(UGroup.getUsers());
        existingUGroup.setUpdatedAt(ZonedDateTime.now());
        return mapToDto(groupRepository.save(existingUGroup));
    }

    private GroupDto mapToDto(UGroup groups) {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(groups.getId());
        groupDto.setName(groups.getName());
        groupDto.setUsers(groups.getUsers().stream().map(User::getUsername).toList());
        return groupDto;
    }
    public SearchResponse<List<GroupDto>> findAll(int page,String search ,String sortBy,String sortDirection) {
        int pageSize =10;
        List<UGroup> UGroups;
        List<GroupDto> groupDtosList;
        long totalGroups;
        if (search != null && !search.isEmpty()) {
            UGroups = groupRepository.findByNameContainingIgnoreCase(search);
            totalGroups= UGroups.size();
        } else {
            UGroups = groupRepository.findAll();
            totalGroups = groupRepository.count();
        }
        if(sortBy!=null){
            Comparator<UGroup> comparator = switch (sortBy.toLowerCase()){
                case "name" -> Comparator.comparing(UGroup::getName);
                case "created_at" -> Comparator.comparing(UGroup::getCreatedAt);
                case "updated_at" -> Comparator.comparing(UGroup::getUpdatedAt);
                default -> throw new IllegalArgumentException("Invalid sortBy parameter: " + sortBy);
            };
            if(sortDirection.equalsIgnoreCase("desc")){
                UGroups.sort(comparator.reversed());
            }
            UGroups.sort(comparator);
        }

        long totalPages = (totalGroups + pageSize - 1) / pageSize;
        if(page!=0){
            List<UGroup> pagedUGroups = UGroups.stream()
                    .skip((long) (page - 1) * pageSize)
                    .limit(pageSize)
                    .toList();
            groupDtosList= pagedUGroups.stream().map(this::mapToDto).collect(Collectors.toList());}
        else {
            groupDtosList= UGroups.stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return new SearchResponse<>(page, totalPages, groupDtosList);
    }


    public GroupDto findById(Long id) {
        return mapToDto(Objects.requireNonNull(groupRepository.findById(id).orElse(null)));
    }

}
