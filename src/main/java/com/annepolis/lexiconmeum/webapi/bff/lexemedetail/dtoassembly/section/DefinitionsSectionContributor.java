package com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.section;

import com.annepolis.lexiconmeum.shared.model.Lexeme;
import com.annepolis.lexiconmeum.shared.model.Sense;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.DefinitionNode;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailResponse;
import com.annepolis.lexiconmeum.webapi.bff.lexemedetail.dtoassembly.LexemeDetailSectionContributor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("definitionsSectionContributor")
class DefinitionsSectionContributor implements LexemeDetailSectionContributor {
    @Override
    public boolean supports(Lexeme lexeme) {
        return true;
    }

    @Override
    public void contribute(Lexeme lexeme, LexemeDetailResponse dto) {
        List<DefinitionNode> roots = new ArrayList<>();
        for (Sense sense : lexeme.getSenses()) {
            List<String> path = sense.getGloss();
            if (!path.isEmpty()) {
                insertPath(roots, path, 0);
            }
        }
        dto.setDefinitions(roots);
    }

    private void insertPath(List<DefinitionNode> siblings, List<String> path, int depth) {
        if (depth >= path.size()) return;
        DefinitionNode node = findOrCreate(siblings, path.get(depth));
        insertPath(node.getChildren(), path, depth + 1);
    }

    private DefinitionNode findOrCreate(List<DefinitionNode> siblings, String text) {
        for (DefinitionNode node : siblings) {
            if (node.getText().equals(text)) return node;
        }
        DefinitionNode newNode = new DefinitionNode(text);
        siblings.add(newNode);
        return newNode;
    }
}
