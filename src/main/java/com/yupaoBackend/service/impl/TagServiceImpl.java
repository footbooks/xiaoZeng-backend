package com.yupaoBackend.service.impl;

import com.yupaoBackend.entity.Tag;
import com.yupaoBackend.mapper.TagMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupaoBackend.service.TagService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标签 服务实现类
 * </p>
 *
 * @author zengjing
 * @since 2024-03-12
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

}
