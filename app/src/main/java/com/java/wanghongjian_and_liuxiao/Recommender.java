package com.java.wanghongjian_and_liuxiao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Recommender {
    final String appID = "com.java.wanghongjian_and_liuxiao";
    static PriorityQueue<Pair> keywords = new PriorityQueue<>(), category = new PriorityQueue<>();
    static HashMap<String, Pair> _k = new HashMap<>(), _c = new HashMap<>();

    Recommender() {
    }

    static void update(News newstoDisplay) {
        for (String w: newstoDisplay.keywords){
            if (!_k.containsKey(w)){
                Pair p = new Pair(w);
                keywords.add(p);
                _k.put(w, p);
            }else{
                Pair p = _k.get(w);
                keywords.remove(p);
                p.value++;
                keywords.add(p);
            }
        }
    }
}

class Pair implements Comparable<Pair> {
    public String key;
    public int value;

    Pair(String _key) {
        key = _key;
        value = 0;
    }

    @Override
    public int compareTo(Pair o) {
        return Integer.compare(this.value, o.value);
    }
}
