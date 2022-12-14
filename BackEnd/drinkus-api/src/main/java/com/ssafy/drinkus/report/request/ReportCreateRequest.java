package com.ssafy.drinkus.report.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCreateRequest {
    private Long toUserId;
    private String reportType;
    private String reportReason;
}
