package com.yupaoBackend.service;

import com.yupaoBackend.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupaoBackend.entity.User;
import com.yupaoBackend.entity.request.TeamJoinRequest;
import com.yupaoBackend.entity.request.TeamQuery;
import com.yupaoBackend.entity.request.TeamQuitRequest;
import com.yupaoBackend.entity.request.TeamUpdateRequest;
import com.yupaoBackend.entity.vo.TeamUserVO;
import java.util.List;

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
     */
    long addTeam(Team team, User loginUser);

    /**
     * 查询队伍信息
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 修改队伍信息
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);
    /**
     * 解散队伍
     */
    boolean deleteTeam(long id, User loginUser);
}
