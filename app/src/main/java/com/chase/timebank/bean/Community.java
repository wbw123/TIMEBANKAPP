package com.chase.timebank.bean;

public class Community {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column dbo.COMMUNITY.COMM_GUID
     *
     * @mbg.generated
     */
    private String commGuid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column dbo.COMMUNITY.COMM_TITLE
     *
     * @mbg.generated
     */
    private String commTitle;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column dbo.COMMUNITY.COMM_ADDRESS
     *
     * @mbg.generated
     */
    private String commAddress;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column dbo.COMMUNITY.COMM_DESP
     *
     * @mbg.generated
     */
    private String commDesp;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column dbo.COMMUNITY.COMM_COORDINATE
     *
     * @mbg.generated
     */
    private String commCoordinate;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column dbo.COMMUNITY.COMM_GUID
     *
     * @return the value of dbo.COMMUNITY.COMM_GUID
     *
     * @mbg.generated
     */
    public String getCommGuid() {
        return commGuid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column dbo.COMMUNITY.COMM_GUID
     *
     * @param commGuid the value for dbo.COMMUNITY.COMM_GUID
     *
     * @mbg.generated
     */
    public void setCommGuid(String commGuid) {
        this.commGuid = commGuid == null ? null : commGuid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column dbo.COMMUNITY.COMM_TITLE
     *
     * @return the value of dbo.COMMUNITY.COMM_TITLE
     *
     * @mbg.generated
     */
    public String getCommTitle() {
        return commTitle;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column dbo.COMMUNITY.COMM_TITLE
     *
     * @param commTitle the value for dbo.COMMUNITY.COMM_TITLE
     *
     * @mbg.generated
     */
    public void setCommTitle(String commTitle) {
        this.commTitle = commTitle == null ? null : commTitle.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column dbo.COMMUNITY.COMM_ADDRESS
     *
     * @return the value of dbo.COMMUNITY.COMM_ADDRESS
     *
     * @mbg.generated
     */
    public String getCommAddress() {
        return commAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column dbo.COMMUNITY.COMM_ADDRESS
     *
     * @param commAddress the value for dbo.COMMUNITY.COMM_ADDRESS
     *
     * @mbg.generated
     */
    public void setCommAddress(String commAddress) {
        this.commAddress = commAddress == null ? null : commAddress.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column dbo.COMMUNITY.COMM_DESP
     *
     * @return the value of dbo.COMMUNITY.COMM_DESP
     *
     * @mbg.generated
     */
    public String getCommDesp() {
        return commDesp;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column dbo.COMMUNITY.COMM_DESP
     *
     * @param commDesp the value for dbo.COMMUNITY.COMM_DESP
     *
     * @mbg.generated
     */
    public void setCommDesp(String commDesp) {
        this.commDesp = commDesp == null ? null : commDesp.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column dbo.COMMUNITY.COMM_COORDINATE
     *
     * @return the value of dbo.COMMUNITY.COMM_COORDINATE
     *
     * @mbg.generated
     */
    public String getCommCoordinate() {
        return commCoordinate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column dbo.COMMUNITY.COMM_COORDINATE
     *
     * @param commCoordinate the value for dbo.COMMUNITY.COMM_COORDINATE
     *
     * @mbg.generated
     */
    public void setCommCoordinate(String commCoordinate) {
        this.commCoordinate = commCoordinate == null ? null : commCoordinate.trim();
    }

    @Override
    public String toString() {
        return "Community{" +
                "commGuid='" + commGuid + '\'' +
                ", commTitle='" + commTitle + '\'' +
                ", commAddress='" + commAddress + '\'' +
                ", commDesp='" + commDesp + '\'' +
                ", commCoordinate='" + commCoordinate + '\'' +
                '}';
    }
}