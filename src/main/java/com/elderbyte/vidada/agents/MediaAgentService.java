package com.elderbyte.vidada.agents;

import com.elderbyte.vidada.agents.local.LocalImageMediaAgent;
import com.elderbyte.vidada.agents.local.LocalTagGuessMediaAgent;
import com.elderbyte.vidada.agents.local.LocalVideoMediaAgent;
import com.elderbyte.vidada.agents.local.LocalXattrMediaAgent;
import com.elderbyte.vidada.tags.TagService;
import com.elderbyte.vidada.tags.autoTag.KeywordBasedTagGuesser;
import com.elderbyte.vidada.video.IVideoAccessService;
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
    private TagService tagService;


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
        registerAgent(new LocalTagGuessMediaAgent(() -> new KeywordBasedTagGuesser(tagService.findAllTags())));
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

}
