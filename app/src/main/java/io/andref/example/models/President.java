package io.andref.example.models;

public class President
{
    private String line1;
    private String line2;
    private int resourceId;

    public President(String line1, String line2, int resourceId)
    {
        this.line1 = line1;
        this.line2 = line2;
        this.resourceId = resourceId;
    }

    public String getLine1()
    {
        return line1;
    }

    public void setLine1(String line1)
    {
        this.line1 = line1;
    }

    public String getLine2()
    {
        return line2;
    }

    public void setLine2(String line2)
    {
        this.line2 = line2;
    }

    public int getResourceId()
    {
        return resourceId;
    }

    public void setResourceId(int resourceId)
    {
        this.resourceId = resourceId;
    }
}