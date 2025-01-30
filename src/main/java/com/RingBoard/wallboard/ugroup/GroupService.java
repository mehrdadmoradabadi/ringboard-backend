package com.RingBoard.wallboard.ugroup;

import com.RingBoard.wallboard.dto.GroupDto;
import com.RingBoard.wallboard.user.User;
import com.RingBoard.wallboard.utils.ResourceNotFoundException;
import com.RingBoard.wallboard.utils.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    public GroupDto save(UGroup UGroup) {
        return mapToDto(groupRepository.save(UGroup));
    }

    public GroupDto findByName(String name) {
        UGroup group = groupRepository.findByName(name);

        if (group == null) {
            throw new ResourceNotFoundException("Group not found with name: " + name);
        }
        return mapToDto(groupRepository.findByName(name));
    }

    public void deleteByName(String name) {
        UGroup group = groupRepository.findByName(name);

        if (group == null) {
            throw new ResourceNotFoundException("Group not found with name: " + name);
        }
        groupRepository.deleteByName(name);
    }

    public void delete(UGroup UGroup) {
        UGroup group = groupRepository.findByName(UGroup.getName());

        if (group == null) {
            throw new ResourceNotFoundException("Group not found with name: " + UGroup);
        }
        groupRepository.delete(UGroup);
    }

    public GroupDto update(UGroup UGroup) {
        UGroup existingUGroup = groupRepository.findById(UGroup.getId()).orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + UGroup.getId()));
        existingUGroup.setName(UGroup.getName());
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
        return groupRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));
    }

}
