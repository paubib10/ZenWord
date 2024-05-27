package com.example.zenworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class Node {
    public Map<Character, Node> fills = new TreeMap<>();
    public boolean isFinalClau;
}

public class TrieMap {
    private Node root = new Node();

    public boolean contains(String clau) {
        Node current = root;
        for (int i = 0; i < clau.length(); i++) {
            Character c = clau.charAt(i);
            Node node = current.fills.get(c);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isFinalClau;
    }

    public boolean add(String clau) {
        Node current = root;
        boolean trobat;
        for (Character c : clau.toCharArray()) {
            Node node = current.fills.get(c);
            if (node == null) {
                node = new Node();
                current.fills.put(c, node);
            }
            current = node;
        }
        trobat = current.isFinalClau;
        current.isFinalClau = true;
        return !trobat;
    }

    // Corrección del método isEmpty()
    public boolean isEmpty() {
        return root.fills.isEmpty();
    }

    public List<String> getTotesLesParaules() {
        List<String> result = new ArrayList<>();
        getObtenirTotesParaules(root, "", result);
        return result;
    }

    private void getObtenirTotesParaules(Node node, String prefix, List<String> result) {
        if (node.isFinalClau) {
            result.add(prefix);
        }
        for (Map.Entry<Character, Node> entry : node.fills.entrySet()) {
            getObtenirTotesParaules(entry.getValue(), prefix + entry.getKey(), result);
        }
    }

    public boolean delete(String clau) {
        return deleteHelper(root, clau, 0);
    }

    private boolean deleteHelper(Node node, String clau, int depth) {
        if (depth == clau.length()) {
            if (!node.isFinalClau) {
                return false; // La palabra no existe
            }
            node.isFinalClau = false;
            return node.fills.isEmpty();
        }

        char c = clau.charAt(depth);
        Node fillNode = node.fills.get(c);
        if (fillNode == null) {
            return false; // La palabra no existe
        }

        boolean fillEliminat = deleteHelper(fillNode, clau, depth + 1);

        if (fillEliminat) {
            node.fills.remove(c);
            return node.fills.isEmpty() && !node.isFinalClau;
        }

        return false;
    }
}