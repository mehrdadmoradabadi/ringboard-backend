package com.wallboard.wallboard.pbx;

import com.wallboard.wallboard.dto.PBXDto;
import com.wallboard.wallboard.utils.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PBXService {
    @Autowired
    private PbxRepository pbxRepository;

    public PBXDto findByName(String name) {
        PBX pbx = pbxRepository.findByName(name);
        if (Objects.isNull(pbx)) {
            return null;
        }
        return mapToDto(pbx);
    }

//    public List<PBX> findAll() {
//        return pbxRepository.findAll();
//    }
    private PBXDto mapToDto(PBX pbx) {
        PBXDto pbxDto = new PBXDto();
        pbxDto.setId(pbx.getId());
        pbxDto.setName(pbx.getName());
        pbxDto.setHost(pbx.getHost());
        pbxDto.setPort(pbx.getPort());
        pbxDto.setProtocol(pbx.getProtocol());
        pbxDto.setUsername(pbx.getUsername());
        return pbxDto;
    }
    public SearchResponse<List<PBXDto>> findAll(int page, String search , String sortBy, String sortDirection) {
        int pageSize = 10;
        List<PBX> pbxs;
        List<PBXDto> pbxDtosList;
        long totalPBXs;
        if (search != null && !search.isEmpty()) {
            pbxs = pbxRepository.findByNameContainingIgnoreCaseOrHostContainingIgnoreCaseOrPortContainingIgnoreCaseOrProtocolContainingIgnoreCaseOrUsernameContainingIgnoreCase(search, search, search, search, search);
            totalPBXs= pbxs.size();
        } else {
            pbxs = pbxRepository.findAll();
            totalPBXs = pbxRepository.count();
        }
        if(sortBy!=null) {
            Comparator<PBX> comparator = switch (sortBy.toLowerCase()) {
                case "name" -> Comparator.comparing(PBX::getName);
                case "host" -> Comparator.comparing(PBX::getHost);
                case "port" -> Comparator.comparing(PBX::getPort);
                case "protocol" -> Comparator.comparing(PBX::getProtocol);
                case "username" -> Comparator.comparing(PBX::getUsername);
                case "created_at" -> Comparator.comparing(PBX::getCreatedAt);
                case "updated_at" -> Comparator.comparing(PBX::getUpdatedAt);
                default -> throw new IllegalArgumentException("Invalid sortBy parameter: " + sortBy);
            };
            if(sortDirection.equalsIgnoreCase("desc")){
                pbxs.sort(comparator.reversed());
            }
            pbxs.sort(comparator);
        }

        long totalPages = (totalPBXs + pageSize - 1) / pageSize;
        if(page!=0){
            List<PBX> pagedPBXs = pbxs.stream()
                    .skip((long) (page - 1) * pageSize)
                    .limit(pageSize)
                    .toList();
            pbxDtosList = pagedPBXs.stream().map(this::mapToDto).collect(Collectors.toList());}
        else{
            pbxDtosList = pbxs.stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return new SearchResponse<>(page,totalPages,pbxDtosList);
    }

    public PBXDto save(PBX pbx) {
        return mapToDto(pbxRepository.save(pbx));
    }

    public void delete(PBX pbx) {
        pbxRepository.delete(pbx);
    }

    public void deleteByName(String name) {
        pbxRepository.deleteByName(name);
    }

    public PBXDto update(PBX pbx) {
        PBX existingPbx = pbxRepository.findByNameOrByHostOrById(pbx.getName());
        assert existingPbx != null;
        existingPbx.setName(pbx.getName());
        existingPbx.setHost(pbx.getHost());
        existingPbx.setPort(pbx.getPort());
        existingPbx.setProtocol(pbx.getProtocol());
        existingPbx.setUsername(pbx.getUsername());
        existingPbx.setPassword(pbx.getPassword());

        return mapToDto(pbxRepository.save(existingPbx));
    }
}
