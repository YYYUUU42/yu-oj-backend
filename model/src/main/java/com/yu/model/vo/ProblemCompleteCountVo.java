package com.yu.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemCompleteCountVo {

    private Long problemId;

    private Integer completionCount;

    private String difficulty;
}
