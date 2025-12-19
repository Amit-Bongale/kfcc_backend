package com.example.KFCC_Backend.DTO;

import com.example.KFCC_Backend.Enum.ApplicationAction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationActionRequestDTO {

    @NotNull
    private ApplicationAction action;
    private String remark;

    public ApplicationAction getAction() {
        return action;
    }

    public void setAction(ApplicationAction action) {
        this.action = action;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
