package org.apache.axis.message;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This was swiped from xerces2, I stripped the comments
 * out to make it easier for me to see what was going on,
 * I'll add them back in later
 */
public class SymbolTable {

    protected static final int TABLE_SIZE = 101;
    protected Entry[] fBuckets = new Entry[TABLE_SIZE];
    public SymbolTable() {}
    
    private ArrayList list = new ArrayList();
    private HashMap hash = new HashMap();
    
    public String getSymbol(int bucket) {
        return (String)list.get(bucket);
        /*
        return fBuckets[bucket].symbol;
        */
    }
    
    public int addSymbol(String symbol) {
        Integer i = (Integer)hash.get(symbol);
        int ret;
        if (i == null) {
            list.add(symbol);
            ret = list.size() - 1;
            hash.put(symbol, new Integer(ret));
        } else {
            ret = i.intValue();
        }
        return ret;
        /*
        int bucket = hash(symbol) % TABLE_SIZE;
        int n = 0;
        OUTER: for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            n++;
            int length = symbol.length();
            if (length == entry.characters.length) {
                for (int i = 0; i < length; i++) {
                    if (symbol.charAt(i) != entry.characters[i]) {
                        continue OUTER;
                    }
                }
                return bucket;
            }
        }
        Entry entry = new Entry(symbol, fBuckets[bucket]);
        fBuckets[bucket] = entry;
        System.out.println("Symbol '" + symbol + "' is index " + bucket);
        return bucket;
        */
    }
    public int addSymbol(char[] buffer, int offset, int length) {
        return addSymbol(new String(buffer, offset, length));
        /*
        int bucket = hash(buffer, offset, length) % TABLE_SIZE;
        int n = 0;
        OUTER: for (Entry entry = fBuckets[bucket]; entry != null; entry = entry.next) {
            n++;
            if (length == entry.characters.length) {
                for (int i = 0; i < length; i++) {
                    if (buffer[offset + i] != entry.characters[i]) {
                        continue OUTER;
                    }
                }
                return bucket;
            }
        }
        Entry entry = new Entry(buffer, offset, length, fBuckets[bucket]);
        fBuckets[bucket] = entry;
        System.out.println("Symbol '" + entry.symbol + "' is index " + bucket);
        return bucket;
        */
    }
    public int hash(String symbol) {
        int code = 0;
        int length = symbol.length();
        for (int i = 0; i < length; i++) {
            code = code * 37 + symbol.charAt(i);
        }
        return code & 0x7FFFFFF;
    }
    public int hash(char[] buffer, int offset, int length) {
        int code = 0;
        for (int i = 0; i < length; i++) {
            code = code * 37 + buffer[offset + i];
        }
        return code & 0x7FFFFFF;
    }
    protected static final class Entry {
        public String symbol;
        public char[] characters;
        public Entry next;
        public Entry(String symbol, Entry next) {
            this.symbol = symbol;
            characters = new char[symbol.length()];
            symbol.getChars(0, characters.length, characters, 0);
            this.next = next;
        }
        public Entry(char[] ch, int offset, int length, Entry next) {
            characters = new char[length];
            System.arraycopy(ch, offset, characters, 0, length);
            symbol = new String(characters);
            this.next = next;
        }
    }
}
