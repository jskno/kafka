package com.linuxacademy.ccdak.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
{
  "activity_id": 14,
  "customer_id": 731,
  "activity_type": "mobile_open",
  "propensity_to_churn": 0.6504426403947733,
  "ip_address": "176.58.219.253"
}
 */
public class InsuranceCustomerActivity {
    @JsonProperty("activity_id")
    private String activityId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("activity_type")
    private String activityType;
    @JsonProperty("propensity_to_churn")
    private Double propensityToChurn;

    @JsonProperty("ip_address")
    private String ipAddress;

    public InsuranceCustomerActivity() {
    }

    public InsuranceCustomerActivity(String activityId, Long customerId, String activityType, Double propensityToChurn,
        String ipAddress) {
        this.activityId = activityId;
        this.customerId = customerId;
        this.activityType = activityType;
        this.propensityToChurn = propensityToChurn;
        this.ipAddress = ipAddress;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Double getPropensityToChurn() {
        return propensityToChurn;
    }

    public void setPropensityToChurn(Double propensityToChurn) {
        this.propensityToChurn = propensityToChurn;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "InsuranceCustomerActivity{" +
            "activityId='" + activityId + '\'' +
            ", customerId=" + customerId +
            ", activityType='" + activityType + '\'' +
            ", propensityToChurn=" + propensityToChurn +
            ", ipAddress='" + ipAddress + '\'' +
            '}';
    }
}

