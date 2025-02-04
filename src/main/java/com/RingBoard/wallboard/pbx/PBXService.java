package com.RingBoard.wallboard.pbx;

import com.RingBoard.wallboard.pbx.dto.PBXDtos;
import com.RingBoard.wallboard.utils.ResourceNotFoundException;
import com.RingBoard.wallboard.utils.SearchResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PBXService {
    @Autowired
    private PbxRepository pbxRepository;

    private PBXDtos.PBXResponse mapToResponse(PBX pbx) {
        PBXDtos.PBXResponse response = new PBXDtos.PBXResponse();
        response.setId(pbx.getId());
        response.setName(pbx.getName());
        response.setHost(pbx.getHost());
        response.setAriPort(pbx.getAriPort());
        response.setAmiPort(pbx.getAmiPort());
        response.setProtocol(pbx.getProtocol());
        response.setUsername(pbx.getUsername());
        response.setCreatedAt(pbx.getCreatedAt());
        response.setUpdatedAt(pbx.getUpdatedAt());
        response.setActive(pbx.isActive());
        response.setAppName(pbx.getAppName());
        return response;
    }
    private PBX mapToEntity(PBXDtos.CreatePBXRequest request) {
        PBX pbx = new PBX();
        pbx.setName(request.getName());
        pbx.setHost(request.getHost());
        pbx.setAriPort(request.getAriPort());
        pbx.setAmiPort(request.getAmiPort());
        pbx.setProtocol(request.getProtocol());
        pbx.setUsername(request.getUsername());
        pbx.setPassword(request.getPassword());
        pbx.setAppName(request.getAppName());
        return pbx;
    }

    public PBXDtos.PBXResponse findByName(String name) {
        PBX pbx = pbxRepository.findByName(name);
        if (Objects.isNull(pbx)) {
            throw new ResourceNotFoundException("PBX not found with name: " + name);
        }
        return mapToResponse(pbx);
    }

    public SearchResponse<List<PBXDtos.PBXResponse>> findAll(int page, String search, String sortBy, String sortDirection) {
        int pageSize = 10;
        List<PBX> pbxs;
        List<PBXDtos.PBXResponse> pbxResponseList;
        long totalPBXs;
        if (search != null && !search.isEmpty()) {
            pbxs = pbxRepository.searchPBX(search);
            totalPBXs= pbxs.size();
        } else {
            pbxs = pbxRepository.findAll();
            totalPBXs = pbxRepository.count();
        }
        if(sortBy!=null) {
            Comparator<PBX> comparator = switch (sortBy.toLowerCase()) {
                case "name" -> Comparator.comparing(PBX::getName);
                case "host" -> Comparator.comparing(PBX::getHost);
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
            pbxResponseList = pagedPBXs.stream().map(this::mapToResponse).collect(Collectors.toList());
        }
        else{
            pbxResponseList = pbxs.stream().map(this::mapToResponse).collect(Collectors.toList());
        }
        return new SearchResponse<>(page, totalPages, pbxResponseList);
    }

    @Transactional
    public PBXDtos.PBXResponse save(PBXDtos.CreatePBXRequest pbxRequest) {
        PBX pbx = mapToEntity(pbxRequest);
        pbx.setCreatedAt(ZonedDateTime.now());
        pbx.setUpdatedAt(ZonedDateTime.now());
        return mapToResponse(pbxRepository.save(pbx));
    }

    @Transactional
    public void delete(PBXDtos.UpdatePBXRequest pbxRequest) {
        PBX existingPbx = pbxRepository.findByNameOrHostOrId(
                pbxRequest.getName(),
                pbxRequest.getHost(),
                pbxRequest.getId()
        );
        if (existingPbx == null) {
            throw new ResourceNotFoundException("PBX not found with name: " + pbxRequest.getName());
        }
        pbxRepository.delete(existingPbx);
    }

    public void deleteByName(String name) {
        PBX existingPbx = pbxRepository.findByName(name);
        if (existingPbx == null) {
            throw new ResourceNotFoundException("PBX not found with name: " + name);
        }
        pbxRepository.deleteByName(name);
    }

    @Transactional
    public PBXDtos.PBXResponse update(PBXDtos.UpdatePBXRequest pbxRequest) {
        PBX existingPbx = pbxRepository.findByNameOrHostOrId(
                pbxRequest.getName(),
                pbxRequest.getHost(),
                pbxRequest.getId()
        );
        if (existingPbx == null) {
            throw new ResourceNotFoundException("PBX not found with name: " + pbxRequest.getName());
        }
        if (pbxRequest.getName() != null) existingPbx.setName(pbxRequest.getName());
        if (pbxRequest.getHost() != null) existingPbx.setHost(pbxRequest.getHost());
        if (pbxRequest.getAriPort() != null) existingPbx.setAriPort(pbxRequest.getAriPort());
        if (pbxRequest.getAmiPort() != null) existingPbx.setAmiPort(pbxRequest.getAmiPort());
        if (pbxRequest.getProtocol() != null) existingPbx.setProtocol(pbxRequest.getProtocol());
        if (pbxRequest.getUsername() != null) existingPbx.setUsername(pbxRequest.getUsername());
        if (pbxRequest.getPassword() != null) existingPbx.setPassword(pbxRequest.getPassword());

        existingPbx.setUpdatedAt(ZonedDateTime.now());

        return mapToResponse(pbxRepository.save(existingPbx));
    }

    public PBX findById(String pbxId) {
        return pbxRepository.findById(Integer.parseInt(pbxId)).orElseThrow(() -> new ResourceNotFoundException("PBX not found with ID: " + pbxId));
    }
}
