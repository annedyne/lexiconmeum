package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class DefinitionNode {

    private final String text;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<DefinitionNode> children = new ArrayList<>();

    public DefinitionNode(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public List<DefinitionNode> getChildren() {
        return children;
    }
}
