package com.yupaoBackend.service;

import com.yupaoBackend.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupaoBackend.entity.User;

/**
 * <p>
 * 队伍 服务类
 * </p>
 *
 * @author zengjing
 * @since 2024-03-12
 */
public interface TeamService extends IService<Team> {
    /**
     * 添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);
}
