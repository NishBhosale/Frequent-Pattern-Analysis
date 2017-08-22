
package PatternMining;


/**
 * Set representation of a string (list of occuring shingles/n-grams), without
 * cardinality.
 * 
 */
public class StringSet {
    private final SparseBooleanVector vector;
    private final KShingling ks;
    
    public StringSet(SparseBooleanVector vector, KShingling ks) {
        this.vector = vector;
        this.ks = ks;
    }
    
    
    public double jaccardSimilarity(StringSet other) throws Exception {
        if (this.ks != other.ks) {
            throw new Exception("Profiles were not created using the same kshingling object!");
        }
        
        return this.vector.jaccard(other.vector);
    }
    
    public double sorensenDiceSimilarity(StringSet other) throws Exception {
        if (this.ks != other.ks) {
            throw new Exception("Profiles were not created using the same kshingling object!");
        }
        
        return 2 * this.vector.intersection(other.vector) / (this.vector.size() + other.vector.size());
    }
}
