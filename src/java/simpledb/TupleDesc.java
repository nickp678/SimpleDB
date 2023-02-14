package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    // List of attributes

    private List<TDItem> tupAttributes;

    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        public final Type fieldType;
        
        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldType = t;
            this.fieldName = n;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // Create iterator object and return the iterator
        Iterator<TDItem> iter = tupAttributes.iterator();
        return iter;
    }

    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    // Iterating through and adding all attributes after creating a TDItem obj for each attribute
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        this.tupAttributes = new ArrayList<TDItem>();
        for(int i = 0; i < typeAr.length; i++){
            TDItem attr = new TDItem(typeAr[i], fieldAr[i]);
            tupAttributes.add(attr);
        }
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    // Same as before except only the second elem of a TDItem is null
    public TupleDesc(Type[] typeAr) {
        tupAttributes = new ArrayList<TDItem>();
        for(int i = 0; i < typeAr.length; i++){
            TDItem attr = new TDItem(typeAr[i], null);
            tupAttributes.add(attr);
        }   
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        return tupAttributes.size();
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        if (i < 0 || i > tupAttributes.size()-1){
            throw new NoSuchElementException(i + ": is an invalid index");
        }
        return tupAttributes.get(i).fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        if (i < 0 || i > tupAttributes.size()-1){
            throw new NoSuchElementException(i + ": is an invalid index");
        }
        return tupAttributes.get(i).fieldType;
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // Took a while to realize that in Java == compares references not the the object itself aha
        for (int i = 0; i < tupAttributes.size(); i++){
            if ( tupAttributes.get(i).fieldName != null && tupAttributes.get(i).fieldName.equals(name)){
                return i;
            }
        }
        throw new NoSuchElementException("No Field of Given Name Found");
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        int size = 0;
        for (int i = 0; i < tupAttributes.size(); i++){
            // If Integer add 4 if string add 128 (defined in Type.java)
            if (tupAttributes.get(i).fieldType == Type.INT_TYPE) size += 4;
            else size += Type.STRING_LEN;
        }
        return size;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        int noElems = td1.numFields() + td2.numFields();
        Type[] typeAr = new Type[noElems];
        String[] fieldAr = new String[noElems];

        int count = 0;
        // two loops inserting all the elements into their respective merged arrays

        for (int i = 0; i < td1.numFields(); i++){
            typeAr[count] = td1.getFieldType(i);
            fieldAr[count] = td1.getFieldName(i);
            count++;
        }
        for (int j = 0; j < td2.numFields(); j++){
            typeAr[count] = td2.getFieldType(j);
            fieldAr[count] = td2.getFieldName(j);
            count++;
        }
        TupleDesc merged = new TupleDesc(typeAr, fieldAr);
        return merged;
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they have the same number of items
     * and if the i-th type in this TupleDesc is equal to the i-th type in o
     * for every i.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */

    public boolean equals(Object o) {
        if (o instanceof TupleDesc){
            TupleDesc obj = (TupleDesc)o;
            if (obj.numFields() != this.numFields()){
                return false;
            }
            else {
                for (int i = 0; i < obj.numFields(); i++){
                    if (!(obj.getFieldType(i).equals(this.getFieldType(i)))) return false;
                }
                return true;
            }
        }
        else return false;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        String out = "";
        String temp = "";
        
        for (int i = 0; i < tupAttributes.size(); i++){
            if (tupAttributes.get(i).fieldType == Type.INT_TYPE) temp = "INT";
            else temp = "STRING";
            out += temp + "(" + this.getFieldName(i) + ")";
            if (i < tupAttributes.size()-1) out += ",";
        }
        return out;
    }
}
