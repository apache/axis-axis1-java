package samples.encoding;

public class Data
{
    public String stringMember;
    public Float floatMember;
    public Data dataMember;
    
    public String toString()
    {
        return getStringVal("");
    }
    
    public String getStringVal(String indent)
    {
        String ret = "\n" + indent + "Data:\n";
        ret +=       indent + " str[" + stringMember + "]\n";
        ret +=       indent + " float[" + floatMember + "]\n";
        ret +=       indent + " data[";
        
        if (dataMember != null)
            ret += dataMember.getStringVal(indent + "  ") + "\n" + indent;
        else
            ret += " null";
        
        ret += " ]";
        return ret;
    }
}
