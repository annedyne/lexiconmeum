package com.annepolis.lexiconmeum.shared;

import java.util.ArrayList;
import java.util.List;

public class Sense {

    private final List<String> glosses;
    private final List<String> tags;

    private Sense(Builder builder){
        this.glosses = builder.glosses;
        this.tags = builder.tags;
    }

    public List<String> getGloss(){
        return glosses;
    }

    public List<String> getTags(){
        return tags;
    }

    public static class Builder {
        final List<String> glosses = new ArrayList<>();
        final List<String> tags= new ArrayList<>();

        public Builder addGloss(String gloss){
            this.glosses.add(gloss);
            return this;
        }

        public Builder addTag(String tag){
            this.tags.add(tag);
            return this;
        }

        public Sense build(){
            return new Sense(this);
        }
    }
}
