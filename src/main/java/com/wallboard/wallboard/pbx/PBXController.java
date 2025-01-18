package com.wallboard.wallboard.pbx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pbx")
public class PBXController {
    @Autowired
    private PBXService pbxService;

    @RequestMapping("/all")
    public List<PBX> findAll() {
        return pbxService.findAll();
    }

    @RequestMapping("/save")
    public PBX save(PBX pbx) {
        return pbxService.save(pbx);
    }

    @RequestMapping("/delete")
    public void delete(PBX pbx) {
        pbxService.delete(pbx);
    }

    @RequestMapping("/deleteByName")
    public void deleteByName(String name) {
        pbxService.deleteByName(name);
    }

    @RequestMapping("/findByName")
    public PBX findByName(String name) {
        return pbxService.findByName(name);
    }

    @RequestMapping("/update")
    public PBX update(PBX pbx) {
        return pbxService.update(pbx);
    }

}
