package com.mz.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    //replace word
    private static final String REPLACEMENT = "***";

    //init root node
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyWord;
            while ((keyWord = reader.readLine()) != null) {
                //add to trie
                this.addKeyWord(keyWord);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load sensitive word file: " + e.getMessage());
        }
    }

    //Add a sensitive word to the prefix tree
    private void addKeyWord(String keyWord) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyWord.length(); i++) {
            char c = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                //init child node
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            //The pointer points to the child node to enter the next cycle
            tempNode = subNode;
            //set end of word
            if (i == keyWord.length() - 1) {
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    //Prefix tree
    private class TrieNode{
        //key word end tag
        private boolean isKeyWordEnd =false;

        //child node
        private Map<Character,TrieNode> subNodes = new HashMap<>();
        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //add children node
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //get children node
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
