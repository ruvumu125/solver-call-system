package com.solver.callbutton.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Company implements Parcelable {

    private Long id;
    private String company_name;
    private String company_address;
    private String created_at;
    private String updated_at;

    public Company(Long id, String company_name, String company_address, String created_at, String updated_at) {
        this.id = id;
        this.company_name = company_name;
        this.company_address = company_address;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
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
    protected Company(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        company_name = in.readString();
        company_address = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
    }

    public static final Creator<Company> CREATOR = new Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(company_name);
        dest.writeString(company_address);
        dest.writeString(created_at);
        dest.writeString(updated_at);
    }
}
