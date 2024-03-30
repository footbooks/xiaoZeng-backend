package com.yupaoBackend.entity.request;
import lombok.Data;

import java.io.Serializable;


/**
 * 通用分页参数
 */
@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = -2910686729171253690L; // 随机生成的序列版本号
    /**
     * 分页大小
     */
    protected int pageSize=20;
    /**
     * 当前页码
     */
    protected int pageNum=2;
}
