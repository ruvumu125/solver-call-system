package com.solver.callbutton.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Office implements Parcelable {

    private Long id;
    private String office_name;
    private Long company_id;
    private String company_name;
    private String agent_name_first_name;
    private String agent_name_last_name;
    private Long agent_id;
    private String created_at;
    private String updated_at;

    // Constructor
    public Office(Long id, String office_name, Long company_id, String company_name,
                  String agent_name_first_name, String agent_name_last_name, Long agent_id,
                  String created_at, String updated_at) {
        this.id = id;
        this.office_name = office_name;
        this.company_id = company_id;
        this.company_name = company_name;
        this.agent_name_first_name = agent_name_first_name;
        this.agent_name_last_name = agent_name_last_name;
        this.agent_id = agent_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    // Empty Constructor
    public Office() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOffice_name() {
        return office_name;
    }

    public void setOffice_name(String office_name) {
        this.office_name = office_name;
    }

    public Long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getAgent_name_first_name() {
        return agent_name_first_name;
    }

    public void setAgent_name_first_name(String agent_name_first_name) {
        this.agent_name_first_name = agent_name_first_name;
    }

    public String getAgent_name_last_name() {
        return agent_name_last_name;
    }

    public void setAgent_name_last_name(String agent_name_last_name) {
        this.agent_name_last_name = agent_name_last_name;
    }

    public Long getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(Long agent_id) {
        this.agent_id = agent_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    // Parcelable implementation
    protected Office(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        office_name = in.readString();
        if (in.readByte() == 0) {
            company_id = null;
        } else {
            company_id = in.readLong();
        }
        company_name = in.readString();
        agent_name_first_name = in.readString();
        agent_name_last_name = in.readString();
        if (in.readByte() == 0) {
            agent_id = null;
        } else {
            agent_id = in.readLong();
        }
        created_at = in.readString();
        updated_at = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(office_name);
        if (company_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(company_id);
        }
        dest.writeString(company_name);
        dest.writeString(agent_name_first_name);
        dest.writeString(agent_name_last_name);
        if (agent_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(agent_id);
        }
        dest.writeString(created_at);
        dest.writeString(updated_at);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Office> CREATOR = new Creator<Office>() {
        @Override
        public Office createFromParcel(Parcel in) {
            return new Office(in);
        }

        @Override
        public Office[] newArray(int size) {
            return new Office[size];
        }
    };
}