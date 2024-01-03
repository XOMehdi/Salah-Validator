package com.wordpress.mmehdi4.salahvalidator;

public class User {

    private String id, name, email, gender;

    // calculate age dynamically
    private int age, totalRecordedPrayers, countValid1, countValid2, countValid3, countValid4, countInvalid1, countInvalid2, countInvalid3, countInvalid4;

    public User() {}
    public User(String id, String name, String email, int age, String gender) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.totalRecordedPrayers = 0;
        this.countValid1 = 0;
        this.countValid2 = 0;
        this.countValid3 = 0;
        this.countValid4 = 0;
        this.countInvalid1 = 0;
        this.countInvalid2 = 0;
        this.countInvalid3 = 0;
        this.countInvalid4 = 0;
    }

    private int getMax(int count1, int count2, int count3, int count4) {
        int max = count1;

        if (max < count2) {
            max = count2;
        }
        if (max < count3) {
            max = count3;
        }
        if (max < count4) {
            max = count4;
        }

        return max;
    }

    private int getMin(int count1, int count2, int count3, int count4) {
        int min = count1;

        if (min > count2) {
            min = count2;
        }
        if (min > count3) {
            min = count3;
        }
        if (min > count4) {
            min = count4;
        }

        return min;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public int getTotalRecordedPrayers() {
        return totalRecordedPrayers;
    }

    public String getValidInvalidRatio() {
        int validPrayers = countValid1 + countValid2 + countValid3 + countValid4;
        int invalidPrayers = countInvalid1 + countInvalid2 + countInvalid3 + countInvalid4;

        return validPrayers + "/" + invalidPrayers;
    }

    public String getMostSuccessfulSalah() {
        int mostSuccessfulSalah =  getMax(countValid1, countValid2, countValid3, countValid4);
        return mostSuccessfulSalah + " Rakah";
    }

    public String getLeastSuccessfulSalah() {
        int leastSuccessfulSalah =  getMin(countInvalid1, countInvalid2, countInvalid3, countInvalid4);
        return leastSuccessfulSalah + " Rakah";
    }

    public void setTotalRecordedPrayers(int totalRecordedPrayers) {
        this.totalRecordedPrayers = totalRecordedPrayers;
    }

    public void setCountValid1(int countValid1) {
        this.countValid1 = countValid1;
    }

    public void setCountValid2(int countValid2) {
        this.countValid2 = countValid2;
    }

    public void setCountValid3(int countValid3) {
        this.countValid3 = countValid3;
    }

    public void setCountValid4(int countValid4) {
        this.countValid4 = countValid4;
    }

    public void setCountInvalid1(int countInvalid1) {
        this.countInvalid1 = countInvalid1;
    }

    public void setCountInvalid2(int countInvalid2) {
        this.countInvalid2 = countInvalid2;
    }

    public void setCountInvalid3(int countInvalid3) {
        this.countInvalid3 = countInvalid3;
    }

    public void setCountInvalid4(int countInvalid4) {
        this.countInvalid4 = countInvalid4;
    }
}
