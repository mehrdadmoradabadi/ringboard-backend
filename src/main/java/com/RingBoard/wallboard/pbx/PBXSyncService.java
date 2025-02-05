package com.RingBoard.wallboard.pbx;

import com.RingBoard.wallboard.resource.ResourceService;
import com.RingBoard.wallboard.resource.adapters.*;
import com.RingBoard.wallboard.resource.dto.ResourceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PBXSyncService {
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private AriService ariService;

    @Transactional
    public void syncPBXResources(String pbxId) {
        try {
            List<QueueInfo> queues = ariService.getQueues(pbxId);
            List<AgentInfo> agents = ariService.getAgents(pbxId);
            List<ExtensionInfo> extensions = ariService.getExtensions(pbxId);
            List<TrunkInfo> trunks = ariService.getTrunks(pbxId);

            for (QueueInfo queue : queues) {
                ResourceDto.CreateResourceRequest request = new ResourceDto.CreateResourceRequest();
                request.setName(queue.getName());
                request.setType("QUEUE");
                request.setPbxId(pbxId);
                resourceService.save(request);
            }

            for (AgentInfo agent : agents) {
                ResourceDto.CreateResourceRequest request = new ResourceDto.CreateResourceRequest();
                request.setName(agent.getName());
                request.setType("AGENT");
                request.setPbxId(pbxId);
                resourceService.save(request);
            }

            for (ExtensionInfo extension : extensions) {
                ResourceDto.CreateResourceRequest request = new ResourceDto.CreateResourceRequest();
                request.setName(extension.getNumber());
                request.setType("EXTENSION");
                request.setPbxId(pbxId);
                resourceService.save(request);
            }

            for (TrunkInfo trunk : trunks) {
                ResourceDto.CreateResourceRequest request = new ResourceDto.CreateResourceRequest();
                request.setName(trunk.getName());
                request.setType("TRUNK");
                request.setPbxId(pbxId);
                resourceService.save(request);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to sync PBX resources: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void refreshPBXResources(String pbxId) {
        resourceService.deleteByPBXId(pbxId);
        syncPBXResources(pbxId);
    }
}