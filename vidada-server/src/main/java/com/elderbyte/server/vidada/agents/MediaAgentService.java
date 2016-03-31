package com.elderbyte.server.vidada.agents;

import com.elderbyte.server.vidada.agents.local.LocalImageMediaAgent;
import com.elderbyte.server.vidada.agents.local.LocalTagGuessMediaAgent;
import com.elderbyte.server.vidada.agents.local.LocalVideoMediaAgent;
import com.elderbyte.server.vidada.agents.local.LocalXattrMediaAgent;
import com.elderbyte.server.vidada.tags.autoTag.CachedTagGuessingBuildService;
import com.elderbyte.server.vidada.video.IVideoAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all media-agents
 */
@Service
public class MediaAgentService {

    private final List<MediaAgent> agents = new ArrayList<>();

    @Autowired
    private IVideoAccessService videoAccessService;

    @Autowired
    private CachedTagGuessingBuildService cachedTagGuessingBuildService;


    public MediaAgentService(){

    }


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


    @PostConstruct
    protected void init(){
        registerAgent(new LocalImageMediaAgent());
        registerAgent(new LocalVideoMediaAgent(videoAccessService));
        registerAgent(new LocalXattrMediaAgent());
        registerAgent(new LocalTagGuessMediaAgent(cachedTagGuessingBuildService));
        // TODO Local xattr agent
    }


    /**
     * Returns all media agents currently registered.
     * @return
     */
    public Iterable<MediaAgent> findAllAgents(){
        return agents;
    }

    /**
     * Register the media agent
     * @param agent
     */
    public void registerAgent(MediaAgent agent){
        agents.add(agent);
    }


    public void refreshAgents() {
        for(MediaAgent agent : findAllAgents()){
            agent.refresh();
        }
    }
}
