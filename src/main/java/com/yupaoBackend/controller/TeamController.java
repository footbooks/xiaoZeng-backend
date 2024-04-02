package com.yupaoBackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupaoBackend.common.BaseResponse;
import com.yupaoBackend.common.ErrorCode;
import com.yupaoBackend.common.ResultUtils;
import com.yupaoBackend.entity.Team;
import com.yupaoBackend.entity.User;
import com.yupaoBackend.entity.UserTeam;
import com.yupaoBackend.entity.request.*;
import com.yupaoBackend.entity.vo.TeamUserVO;
import com.yupaoBackend.exception.BusinessException;
import com.yupaoBackend.service.TeamService;
import com.yupaoBackend.service.UserService;
import com.yupaoBackend.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@Slf4j
@CrossOrigin(allowCredentials = "true")
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private UserTeamService userTeamService;

    /**
     * 创建队伍
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest,HttpServletRequest request){
        if (teamAddRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        User loginUser = userService.getLoginUser(request);
        long teamId = teamService.addTeam(team,loginUser);
        if (teamId<=0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"添加队伍失败");
        }
        return ResultUtils.success(team.getId());
    }

    /**
     * 删除（解散）队伍
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(long teamId,HttpServletRequest request){
        if (teamId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean delete = teamService.deleteTeam(teamId,loginUser);
        if(!delete){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 修改队伍信息
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        if(teamUpdateRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        boolean update = teamService.updateTeam(teamUpdateRequest,loginUser);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"修改失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据队伍id获取队伍信息
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(@RequestParam("id") long teamId){
        if (teamId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",teamId);
        Team team = teamService.getOne(queryWrapper);
        if (team==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 查询所有队伍信息
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断当前用户是否为管理员
        boolean isAdmin = userService.isAdmin(request);
        // 1、查询队伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(teamIdList) || teamIdList.size()<=0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        // 2、判断当前用户是否已加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            userTeamQueryWrapper.eq("user_id", loginUser.getId());
            userTeamQueryWrapper.in("team_id", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            // 已加入的队伍 id 集合（取出该用户已加入的队伍id）
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team -> {
                //遍历队伍列表，如果该用户已经加入该队伍，在队伍，用户关系里面设置该用户已加入队伍
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 3、查询已加入队伍的人数
        //取出每个队伍的队伍用户数据
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("team_id", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size()<=0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        //key为teamId，value的已加入该队伍的用户队伍信息
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        //设置队伍已加入人数
        teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size()));
        return ResultUtils.success(teamList);
    }

    /**
     * 分页查询
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(@RequestBody TeamQuery teamQuery){
        if (teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> teamPage = teamService.page(page, queryWrapper);
        if (teamPage.getSize()==0){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(teamPage);
    }

    /**
     * 加入队伍
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest,loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 退出队伍
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest,loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我创建的队伍
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList);
    }


    /**
     * 获取我加入的队伍
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Boolean isAdmin = userService.isAdmin(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 取出不重复的队伍 id
        // teamId userId
        // 1, 2
        // 1, 3
        // 2, 3
        // result
        // 1 => 2, 3
        // 2 => 3
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamUserVO> teamList = new ArrayList<>();
        if(idList.size()<=0 && !isAdmin){
            return ResultUtils.success(teamList);
        }
        teamList = teamService.listTeams(teamQuery,true);
        //如果有返回值，统一设置属性为已加入
        teamList.stream().peek(teamUserVO ->teamUserVO.setHasJoin(true)).collect(Collectors.toList());
        return ResultUtils.success(teamList);
    }

}
