package com.yu.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目提交
 *
 * @author Shier
 * @TableName question_submit
 */
@TableName(value = "question_submit")
@Data
public class QuestionSubmit implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 判题信息（json 对象）
     */
    private String judgeInfo;

    /**
     * 编程语言
     */
    private String submitLanguage;

    /**
     * 用户提交代码
     */
    private String submitCode;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer submitState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 题目难度
     */
    private String difficulty;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}