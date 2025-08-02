package com.annepolis.lexiconmeum.shared.model;

import com.annepolis.lexiconmeum.shared.model.grammar.GrammaticalGender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Sense {

    private final List<String> glosses;

    private Sense(Builder builder ){
        this.glosses = builder.glosses;
    }

    public List<String> getGloss(){
        return glosses;
    }

    public static class Builder {
        final List<String> glosses = new ArrayList<>();
        final Set<GrammaticalGender> genders = new TreeSet<>();

        public Builder addGloss(String gloss){
            this.glosses.add(gloss);
            return this;
        }

        public Builder addGender(GrammaticalGender gender){
            this.genders.add(gender);
            return this;
        }

        public Sense build(){
            return new Sense(this);
        }
    }
}
