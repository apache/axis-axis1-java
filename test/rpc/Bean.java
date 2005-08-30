
package test.rpc;

import java.util.Calendar;

public class Bean implements IF1
{
    protected String id;
    protected String title;
    protected String category;
    protected Calendar date;

    public Bean()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String aId)
    {
        id = aId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String aTitle)
    {
        title = aTitle;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String aCategory)
    {
        category = aCategory;
    }
    
    public Calendar getDate()
    {
        return date;
    }

    public void setDate(Calendar aDate)
    {
        date = aDate;
    }
}

