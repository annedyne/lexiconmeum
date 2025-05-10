package com.annepolis.lexiconmeum.shared;

import java.util.ArrayList;
import java.util.List;

public class Sense {

    List<String> glosses;
    List<String> tags;

    public void addGloss(String gloss){
        if(glosses == null){
            glosses = new ArrayList<>();
        }
        glosses.add(gloss);
    }

    public void addTag(String tag){
        if(tags == null){
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }


}
