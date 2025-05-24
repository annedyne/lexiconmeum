package com.annepolis.lexiconmeum.shared;

import java.util.ArrayList;
import java.util.List;

public class Sense {

    List<String> glosses = new ArrayList<>();
    List<String> tags= new ArrayList<>();

    public void addGloss(String gloss){
        glosses.add(gloss);
    }

    public void addTag(String tag){
        tags.add(tag);
    }


}
