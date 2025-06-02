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

    public static class Builder {
        List<String> glosses = new ArrayList<>();
        List<String> tags= new ArrayList<>();

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
