package com.wallboard.wallboard.pbx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PBXService {
    @Autowired
    private PbxRepository pbxRepository;

    public PBX findByName(String name) {
        return pbxRepository.findByName(name);
    }

    public List<PBX> findAll() {
        return pbxRepository.findAll();
    }

    public PBX save(PBX pbx) {
        return pbxRepository.save(pbx);
    }

    public void delete(PBX pbx) {
        pbxRepository.delete(pbx);
    }

    public void deleteByName(String name) {
        pbxRepository.deleteByName(name);
    }

    public PBX update(PBX pbx) {
        return pbxRepository.save(pbx);
    }
}
