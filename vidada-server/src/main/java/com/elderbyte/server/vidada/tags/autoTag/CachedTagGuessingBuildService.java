package com.elderbyte.server.vidada.tags.autoTag;

import com.elderbyte.server.vidada.tags.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.function.Supplier;

/**
 *
 */
@Service
public class CachedTagGuessingBuildService implements Supplier<ITagGuessingStrategy> {

    private KeywordBasedTagGuesser cachedGuesser = null;
    private final Object guesserLock = new Object();

    @Autowired
    private TagService tagService;

    @PostConstruct
    public void init(){

    }

    @Override
    public ITagGuessingStrategy get() {
        synchronized (guesserLock){
            if(cachedGuesser == null){
                cachedGuesser = new KeywordBasedTagGuesser(tagService.findAllTags());
            }
            return cachedGuesser;
        }
    }

    public void refresh(){
        synchronized (guesserLock) {
            cachedGuesser = null;
        }
    }
}
