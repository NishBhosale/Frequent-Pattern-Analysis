

package PatternMining;


/**
 * Abstract class for string similarities that rely on set operations (like 
 * cosine similarity or jaccard index). This abstract base class cares for
 * converting input strings into sets of n-grams (aka k-shingles).
 
 */
abstract class ShingleBased {
    protected final int k;
    
    /**
     * 
     * @param k 
     */
    public ShingleBased(int k) {
        this.k = k;
    }
    
    /**
     * 
     */
    public ShingleBased() {
        this(3);
    }
    
    /**
     * Return k, the length of k-shingles (aka n-grams)
     * @return 
     */
    public int getK() {
        return k;
    }

}
